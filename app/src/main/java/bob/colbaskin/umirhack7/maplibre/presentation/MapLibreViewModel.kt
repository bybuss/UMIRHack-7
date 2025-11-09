package bob.colbaskin.umirhack7.maplibre.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.maplibre.domain.OfflineMapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineRegion

private const val TAG = "MapLibre"

@HiltViewModel
class MapLibreViewModel @Inject constructor(
    private val repository: OfflineMapRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapLibreState())
    val state: StateFlow<MapLibreState> = _state.asStateFlow()

    private var downloadJob: Job? = null
    private var currentDownloadRegion: OfflineRegion? = null

    init {
        loadOfflineRegions()
    }

    fun onAction(action: MapLibreAction) {
        when (action) {
            MapLibreAction.LoadOfflineRegions -> loadOfflineRegions()
            MapLibreAction.DownloadMoscowMap -> downloadMoscowMap()
            MapLibreAction.CancelDownload -> cancelDownload()
            MapLibreAction.ClearError -> clearError()
            is MapLibreAction.DeleteRegion -> deleteRegion(action.regionId)
        }
    }

    private fun loadOfflineRegions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val regions = repository.getAllOfflineRegions()
                _state.update {
                    it.copy(
                        offlineRegions = regions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading offline regions: ${e.message}")
                _state.update {
                    it.copy(
                        error = "Failed to load regions: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun downloadMoscowMap() {
        downloadJob?.cancel()

        _state.update {
            it.copy(
                isDownloading = true,
                downloadProgress = 0f,
                error = null
            )
        }

        downloadJob = viewModelScope.launch {
            try {
                Log.d(TAG, "Starting Moscow map download")
                val bounds = LatLngBounds.Builder()
                    .include(LatLng(55.7558, 37.6173))
                    .include(LatLng(55.8558, 37.7173))
                    .build()

                val region = repository.downloadRegion(
                    styleUrl = "https://tiles.openfreemap.org/styles/liberty",
                    bounds = bounds,
                    minZoom = 0.0,
                    maxZoom = 30.0,
                    regionName = "Москва"
                )

                currentDownloadRegion = region
                monitorDownloadProgress(region)

            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}")
                _state.update {
                    it.copy(
                        isDownloading = false,
                        error = "Download failed: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun monitorDownloadProgress(region: OfflineRegion) {
        var lastProgressTime = System.currentTimeMillis()
        var lastCompletedCount = 0L

        while (_state.value.isDownloading) {
            try {
                val status = repository.getDownloadStatus(region)
                val progress = if (status.requiredResourceCount > 0) {
                    status.completedResourceCount.toFloat() / status.requiredResourceCount.toFloat()
                } else {
                    0f
                }

                _state.update { it.copy(downloadProgress = progress.coerceIn(0f, 1f)) }
                Log.d(TAG, "Download progress: ${(progress * 100).toInt()}%")

                if (status.isComplete) {
                    Log.d(TAG, "Download completed successfully")
                    _state.update {
                        it.copy(
                            isDownloading = false,
                            downloadProgress = 1f
                        )
                    }
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
                if (_state.value.isDownloading) {
                    Log.e(TAG, "Error during download: ${e.message}")
                    _state.update {
                        it.copy(
                            isDownloading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
                break
            }
        }
    }

    private fun cancelDownload() {
        Log.d(TAG, "Download cancelled by user")
        downloadJob?.cancel()
        _state.update {
            it.copy(
                isDownloading = false,
                downloadProgress = 0f
            )
        }
        currentDownloadRegion = null
    }

    private fun deleteRegion(regionId: Long) {
        viewModelScope.launch {
            try {
                val region = _state.value.offlineRegions.find { it.id == regionId }
                region?.let {
                    repository.deleteRegion(it)
                    loadOfflineRegions()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete region: ${e.message}") }
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}