package bob.colbaskin.umirhack7.maplibre.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.takeIfSuccess
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.data.notifocation.MapDownloadService
import bob.colbaskin.umirhack7.maplibre.data.sync.SyncManager
import bob.colbaskin.umirhack7.maplibre.domain.location.LocationRepository
import bob.colbaskin.umirhack7.maplibre.domain.NotificationRepository
import bob.colbaskin.umirhack7.maplibre.domain.OfflineMapRepository
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsRepository
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.BOUNDS_PADDING
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.DEFAULT_MAX_ZOOM
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.DEFAULT_MIN_ZOOM
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.MAP_STYLE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineRegion

private const val TAG = "MapLibre"

@HiltViewModel
class MapLibreViewModel @Inject constructor(
    private val repository: OfflineMapRepository,
    private val locationRepository: LocationRepository,
    private val notificationRepository: NotificationRepository,
    private val fieldsRepository: FieldsRepository,
    private val syncManager: SyncManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var state by mutableStateOf(MapLibreState())
        private set

    private var downloadJob: Job? = null
    private var currentDownloadRegion: OfflineRegion? = null
    private var currentUserLocation: LatLng? = null
    private var isDownloadCancelled = false
    private var broadcastReceived = false

    private val downloadCancelledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MapDownloadService.ACTION_DOWNLOAD_CANCELLED) {
                if (!broadcastReceived) {
                    broadcastReceived = true
                    Log.d(TAG, "Download cancelled from notification")
                    if (!isDownloadCancelled) {
                        isDownloadCancelled = true
                        cancelDownload()
                    }
                }
            }
        }
    }

    init {
        loadOfflineRegions()
        checkForIncompleteDownloads()
        setupFieldsStream()
        setupSync()
        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter(MapDownloadService.ACTION_DOWNLOAD_CANCELLED)

        ContextCompat.registerReceiver(
            context,
            downloadCancelledReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(downloadCancelledReceiver)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        downloadJob?.cancel()
    }

    fun onAction(action: MapLibreAction) {
        when (action) {
            MapLibreAction.LoadOfflineRegions -> loadOfflineRegions()
            MapLibreAction.DownloadCurrentRegion -> downloadCurrentRegion()
            MapLibreAction.CancelDownload -> cancelDownload()
            MapLibreAction.ClearError -> clearError()
            MapLibreAction.GetCurrentLocation -> getCurrentLocation()
            MapLibreAction.DismissRegionSuggestion -> dismissRegionSuggestion()
            is MapLibreAction.DeleteRegion -> deleteRegion(action.regionId)
            MapLibreAction.CloseFabMenu -> {
                state = state.copy(isFabExpanded = false)
            }
            MapLibreAction.ToggleFabExpand -> {
                state = state.copy(isFabExpanded = !state.isFabExpanded)
            }
            MapLibreAction.LoadFields -> syncFields()
            MapLibreAction.ForceSync -> syncFields()
            MapLibreAction.ToggleFieldsVisibility -> {
                state = state.copy(showFields = !state.showFields)
            }
            is MapLibreAction.SelectField -> {
                val fieldCenter = calculateFieldCenter(action.field)
                state = state.copy(
                    selectedField = action.field,
                    cameraTarget = fieldCenter
                )
            }
            MapLibreAction.ClearSelectedField -> {
                state = state.copy(selectedField = null, cameraTarget = null)
            }
            is MapLibreAction.FieldClicked -> {
                val fieldCenter = calculateFieldCenter(action.field)
                state = state.copy(
                    selectedField = action.field,
                    cameraTarget = fieldCenter
                )
            }
        }
    }

    fun calculateFieldCenter(field: Field): LatLng {
        val vertices = field.geometry.toLatLngList()
        if (vertices.isEmpty()) return LatLng(0.0, 0.0)

        var minLat = Double.MAX_VALUE
        var maxLat = -Double.MAX_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = -Double.MAX_VALUE

        vertices.forEach { latLng ->
            minLat = minOf(minLat, latLng.latitude)
            maxLat = maxOf(maxLat, latLng.latitude)
            minLon = minOf(minLon, latLng.longitude)
            maxLon = maxOf(maxLon, latLng.longitude)
        }

        return LatLng(
            (minLat + maxLat) / 2,
            (minLon + maxLon) / 2
        )
    }

    private fun setupFieldsStream() {
        viewModelScope.launch {
            fieldsRepository.getFieldsStream().collect { fields ->
                state = state.copy(fieldsState = UiState.Success(fields))
            }
        }
    }

    private fun setupSync() {
        viewModelScope.launch {
            syncFields()
        }
        syncManager.scheduleSync()
    }

    private fun syncFields() {
        state = state.copy(isSyncing = true, syncState = UiState.Loading)

        viewModelScope.launch {
            when (val result = fieldsRepository.syncFields()) {
                is ApiResult.Success -> {
                    state = state.copy(
                        isSyncing = false,
                        syncState = UiState.Success(Unit)
                    )
                    Log.d(TAG, "Синхронизация завершена успешно")
                }
                is ApiResult.Error -> {
                    state = state.copy(
                        isSyncing = false,
                        syncState = UiState.Error(
                            title = "Ошибка синхронизации",
                            text = result.text
                        )
                    )
                    Log.e(TAG, "Ошибка синхронизации: ${result.text}")
                }
            }
        }

    }

    private fun loadFields() {
        state = state.copy(fieldsState = UiState.Loading)

        viewModelScope.launch {
            try {
                val result = fieldsRepository.getFieldsList()
                when (result) {
                    is ApiResult.Success -> {
                        Log.d(TAG, "Fields loaded successfully: ${result.data.size} fields")
                        state = state.copy(fieldsState = UiState.Success(result.data))
                    }
                    is ApiResult.Error -> {
                        Log.e(TAG, "Error loading fields: ${result.text}")
                        state = state.copy(
                            fieldsState = UiState.Error(
                                title = "Ошибка загрузки полей",
                                text = result.text
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading fields: ${e.message}")
                state = state.copy(
                    fieldsState = UiState.Error(
                        title = "Ошибка загрузки полей",
                        text = e.message ?: "Неизвестная ошибка"
                    )
                )
            }
        }
    }

    private fun loadOfflineRegions() {
        state = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val allRegions = repository.getAllOfflineRegions()

                val completedRegions = allRegions.filter { region ->
                    try {
                        val status = repository.getDownloadStatus(region)
                        status.isComplete
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                        false
                    }
                }

                state = state.copy(
                    regionsState = UiState.Success(completedRegions),
                    isLoading = false
                )

                checkRegionSuggestion(completedRegions)

            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun checkForIncompleteDownloads() {
        viewModelScope.launch {
            try {
                val allRegions = repository.getAllOfflineRegions()
                val incompleteRegions = allRegions.filter { region ->
                    try {
                        val status = repository.getDownloadStatus(region)
                        !status.isComplete
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                        false
                    }
                }

                incompleteRegions.forEach { region ->
                    try {
                        repository.deleteRegion(region)
                        Log.d(TAG, "Deleted incomplete region: ${region.id}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting incomplete region: ${e.message}")
                    }
                }

                if (incompleteRegions.isNotEmpty()) {
                    loadOfflineRegions()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error checking incomplete downloads: ${e.message}")
            }
        }
    }

    private fun getCurrentLocation() {
        state = state.copy(
            locationState = state.locationState.copy(
                isLoading = true,
                error = null
            )
        )

        viewModelScope.launch {
            try {
                val location = locationRepository.getCurrentLocation()
                currentUserLocation = location

                if (location != null) {
                    val cityName = locationRepository.getCityName(location)

                    state = state.copy(
                        locationState = state.locationState.copy(
                            currentLocation = location,
                            cityName = cityName,
                            isLoading = false
                        )
                    )

                    val regions = state.regionsState.takeIfSuccess() ?: emptyList()
                    checkRegionSuggestion(regions, cityName)

                } else {
                    state = state.copy(
                        locationState = state.locationState.copy(
                            isLoading = false,
                            error = "Не удалось получить местоположение"
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting location: ${e.message}")
                state = state.copy(
                    locationState = state.locationState.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка получения местоположения"
                    )
                )
            }
        }
    }

    private fun checkRegionSuggestion(
        regions: List<OfflineRegion>,
        cityName: String? = null
    ) {
        val targetCityName = cityName ?: state.locationState.cityName
        val hasLocation = currentUserLocation != null

        if (hasLocation && targetCityName != null) {
            val regionExists = regions.any { region ->
                val regionName = String(region.metadata)
                regionName.contains(targetCityName, ignoreCase = true)
            }

            if (!regionExists) {
                state = state.copy(
                    showRegionSuggestion = true,
                    suggestedRegionName = targetCityName
                )
            }
        }
    }

    private fun downloadCurrentRegion() {
        val location = currentUserLocation
        val regionName = state.suggestedRegionName ?: "Текущий регион"

        if (location == null) {
            viewModelScope.launch {
                notificationRepository.showDownloadError(regionName, "Не удалось определить местоположение")
            }
            return
        }

        downloadJob?.cancel()

        state = state.copy(
            isDownloading = true,
            downloadProgress = 0f,
            downloadState = UiState.Loading,
            showRegionSuggestion = false
        )

        viewModelScope.launch {
            notificationRepository.startDownloadService(regionName)
        }

        downloadJob = viewModelScope.launch {
            try {
                Log.d(TAG, "Starting current region download: $regionName")

                val bounds = LatLngBounds.Builder()
                    .include(LatLng(location.latitude - BOUNDS_PADDING, location.longitude - BOUNDS_PADDING))
                    .include(LatLng(location.latitude + BOUNDS_PADDING, location.longitude + BOUNDS_PADDING))
                    .build()

                val region = repository.downloadRegion(
                    styleUrl = MAP_STYLE_URL,
                    bounds = bounds,
                    minZoom = DEFAULT_MIN_ZOOM,
                    maxZoom = DEFAULT_MAX_ZOOM,
                    regionName = regionName
                )

                state = state.copy(currentDownloadRegionId = region.id)
                currentDownloadRegion = region
                monitorDownloadProgress(region, regionName)

            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}")
                handleDownloadError(e, regionName)
            }
        }
    }

    private fun monitorDownloadProgress(region: OfflineRegion, regionName: String) {
        var lastProgressTime = System.currentTimeMillis()
        var lastCompletedCount = 0L

        viewModelScope.launch {
            while (state.isDownloading) {
                try {
                    val status = repository.getDownloadStatus(region)
                    val progress = if (status.requiredResourceCount > 0) {
                        status.completedResourceCount.toFloat() / status.requiredResourceCount.toFloat()
                    } else {
                        0f
                    }

                    val progressPercent = (progress * 100).toInt()
                    state = state.copy(downloadProgress = progress)

                    notificationRepository.updateDownloadProgress(regionName, progressPercent)
                    Log.d(TAG, "Download progress: $progressPercent%")

                    if (status.isComplete) {
                        Log.d(TAG, "Download completed successfully")
                        state = state.copy(
                            isDownloading = false,
                            downloadProgress = 1f,
                            downloadState = UiState.Success(Unit),
                            currentDownloadRegionId = null
                        )

                        notificationRepository.completeDownload(regionName)
                        loadOfflineRegions()
                        break
                    }

                    val currentTime = System.currentTimeMillis()
                    if (status.completedResourceCount > lastCompletedCount) {
                        lastProgressTime = currentTime
                        lastCompletedCount = status.completedResourceCount
                    } else if (currentTime - lastProgressTime > 30000) {
                        throw Exception("Нет прогресса в течение 30 секунд - проверьте соединение")
                    }

                    if (currentTime - lastProgressTime > 600000) {
                        throw Exception("Таймаут загрузки")
                    }

                    delay(1000)

                } catch (e: Exception) {
                    if (state.isDownloading) {
                        handleDownloadError(e, regionName)
                    }
                    break
                }
            }
        }
    }

    private fun cancelDownload() {
        Log.d(TAG, "Download cancelled by user")
        downloadJob?.cancel()

        currentDownloadRegion?.let { region ->
            viewModelScope.launch {
                try {
                    repository.deleteRegion(region)
                    Log.d(TAG, "Region deleted after cancel")
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting region after cancel: ${e.message}")
                }
            }
        }

        state = state.copy(
            isDownloading = false,
            downloadProgress = 0f,
            downloadState = UiState.Loading,
            currentDownloadRegionId = null
        )
        currentDownloadRegion = null

        viewModelScope.launch {
            notificationRepository.cancelDownloadNotification()
        }
    }

    private fun handleDownloadError(e: Exception, regionName: String) {
        Log.e(TAG, "Error during download: ${e.message}")

        val userFriendlyError = getErrorDescription(e)

        currentDownloadRegion?.let { region ->
            viewModelScope.launch {
                try {
                    repository.deleteRegion(region)
                    Log.d(TAG, "Region deleted after error")
                } catch (deleteException: Exception) {
                    Log.e(TAG, "Error deleting region after error: ${deleteException.message}")
                }
            }
        }

        viewModelScope.launch {
            notificationRepository.showDownloadError(regionName, userFriendlyError)
        }

        state = state.copy(
            isDownloading = false,
            downloadState = UiState.Error(
                title = "Ошибка загрузки",
                text = userFriendlyError
            ),
            currentDownloadRegionId = null
        )
        currentDownloadRegion = null
    }

    private fun getErrorDescription(exception: Exception): String {
        return when {
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Проверьте подключение к интернету и повторите попытку"
            exception.message?.contains("timeout", ignoreCase = true) == true ->
                "Загрузка заняла слишком много времени. Попробуйте еще раз"
            exception.message?.contains("storage", ignoreCase = true) == true ->
                "Недостаточно места на устройстве. Освободите место и попробуйте снова"
            exception.message?.contains("Нет прогресса", ignoreCase = true) == true ->
                "Загрузка остановилась. Проверьте стабильность интернет-соединения"
            else -> "Попробуйте повторить загрузку позже"
        }
    }

    private fun deleteRegion(regionId: Long) {
        viewModelScope.launch {
            try {
                val regions = state.regionsState.takeIfSuccess() ?: return@launch
                val region = regions.find { it.id == regionId }
                region?.let {
                    repository.deleteRegion(it)
                    loadOfflineRegions()
                }
            } catch (e: Exception) {
                state = state.copy(
                    regionsState = UiState.Error(
                        title = "Ошибка удаления",
                        text = e.message ?: "Неизвестная ошибка"
                    )
                )
            }
        }
    }

    private fun dismissRegionSuggestion() {
        state = state.copy(showRegionSuggestion = false)
    }

    private fun clearError() {
        state = state.copy(
            regionsState = when (val current = state.regionsState) {
                is UiState.Error -> UiState.Loading
                else -> current
            },
            downloadState = when (val current = state.downloadState) {
                is UiState.Error -> UiState.Loading
                else -> current
            },
            locationState = state.locationState.copy(error = null)
        )
    }
}
