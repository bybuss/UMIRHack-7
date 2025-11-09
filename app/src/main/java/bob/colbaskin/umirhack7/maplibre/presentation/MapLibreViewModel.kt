package bob.colbaskin.umirhack7.maplibre.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.takeIfSuccess
import bob.colbaskin.umirhack7.maplibre.domain.LocationRepository
import bob.colbaskin.umirhack7.maplibre.domain.OfflineMapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val locationRepository: LocationRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    var state by mutableStateOf(MapLibreState())
        private set

    private var downloadJob: Job? = null
    private var currentDownloadRegion: OfflineRegion? = null
    private var currentUserLocation: LatLng? = null

    init {
        loadOfflineRegions()
    }

    fun onAction(action: MapLibreAction) {
        when (action) {
            MapLibreAction.LoadOfflineRegions -> loadOfflineRegions()
            MapLibreAction.DownloadCurrentRegion -> downloadCurrentRegion()
            MapLibreAction.CancelDownload -> cancelDownload()
            MapLibreAction.ClearError -> clearError()
            MapLibreAction.RequestLocationPermission -> {
                state = state.copy(
                    locationState = state.locationState.copy(
                        hasPermission = true
                    )
                )
            }
            MapLibreAction.GetCurrentLocation -> getCurrentLocation()
            MapLibreAction.DismissRegionSuggestion -> dismissRegionSuggestion()
            is MapLibreAction.DeleteRegion -> deleteRegion(action.regionId)
            MapLibreAction.CloseFabMenu -> {
                state = state.copy(isFabExpanded = false)
            }
            MapLibreAction.ToggleFabExpand -> {
                state = state.copy(isFabExpanded = !state.isFabExpanded)
            }
        }
    }

    private fun loadOfflineRegions() {
        state = state.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val regions = repository.getAllOfflineRegions()
                state = state.copy(
                    regionsState = UiState.Success(regions),
                    isLoading = false
                )

                checkRegionSuggestion(regions)

            } catch (e: Exception) {
                Log.e(TAG, "Error loading offline regions: ${e.message}")
                state = state.copy(
                    regionsState = UiState.Error(
                        title = "Ошибка загрузки",
                        text = e.message ?: "Неизвестная ошибка"
                    ),
                    isLoading = false
                )
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
            state = state.copy(
                downloadState = UiState.Error(
                    title = "Ошибка",
                    text = "Не удалось определить местоположение"
                )
            )
            return
        }

        downloadJob?.cancel()

        state = state.copy(
            isDownloading = true,
            downloadProgress = 0f,
            downloadState = UiState.Loading,
            showRegionSuggestion = false
        )

        downloadJob = viewModelScope.launch {
            try {
                Log.d(TAG, "Starting current region download: $regionName")

                val bounds = LatLngBounds.Builder()
                    .include(LatLng(location.latitude - 0.18, location.longitude - 0.18))
                    .include(LatLng(location.latitude + 0.18, location.longitude + 0.18))
                    .build()

                val region = repository.downloadRegion(
                    styleUrl = "https://tiles.openfreemap.org/styles/liberty",
                    bounds = bounds,
                    minZoom = 0.0,
                    maxZoom = 30.0,
                    regionName = regionName
                )

                currentDownloadRegion = region
                monitorDownloadProgress(region)

            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}")
                state = state.copy(
                    isDownloading = false,
                    downloadState = UiState.Error(
                        title = "Ошибка загрузки",
                        text = e.message ?: "Неизвестная ошибка"
                    )
                )
            }
        }
    }

    private suspend fun monitorDownloadProgress(region: OfflineRegion) {
        var lastProgressTime = System.currentTimeMillis()
        var lastCompletedCount = 0L

        while (state.isDownloading) {
            try {
                val status = repository.getDownloadStatus(region)
                val progress = if (status.requiredResourceCount > 0) {
                    status.completedResourceCount.toFloat() / status.requiredResourceCount.toFloat()
                } else {
                    0f
                }

                state = state.copy(downloadProgress = progress.coerceIn(0f, 1f))
                Log.d(TAG, "Download progress: ${(progress * 100).toInt()}%")

                if (status.isComplete) {
                    Log.d(TAG, "Download completed successfully")
                    state = state.copy(
                        isDownloading = false,
                        downloadProgress = 1f,
                        downloadState = UiState.Success(Unit)
                    )
                    loadOfflineRegions()
                    break
                }

                val currentTime = System.currentTimeMillis()
                if (status.completedResourceCount > lastCompletedCount) {
                    lastProgressTime = currentTime
                    lastCompletedCount = status.completedResourceCount
                } else if (currentTime - lastProgressTime > 30000) {
                    throw Exception("No progress for 30 seconds - check connection")
                }

                if (currentTime - lastProgressTime > 600000) {
                    throw Exception("Download timeout")
                }

                delay(1000)

            } catch (e: Exception) {
                if (state.isDownloading) {
                    Log.e(TAG, "Error during download: ${e.message}")
                    state = state.copy(
                        isDownloading = false,
                        downloadState = UiState.Error(
                            title = "Ошибка загрузки",
                            text = e.message ?: "Неизвестная ошибка"
                        )
                    )
                }
                break
            }
        }
    }

    private fun cancelDownload() {
        Log.d(TAG, "Download cancelled by user")
        downloadJob?.cancel()
        state = state.copy(
            isDownloading = false,
            downloadProgress = 0f,
            downloadState = UiState.Loading
        )
        currentDownloadRegion = null
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
