package bob.colbaskin.umirhack7.maplibre.presentation

import org.maplibre.android.offline.OfflineRegion

data class MapLibreState(
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val offlineRegions: List<OfflineRegion> = emptyList(),
    val error: String? = null
) {
    val showDownloadScreen: Boolean
        get() = isDownloading
}