package bob.colbaskin.umirhack7.maplibre.presentation

import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.LocationState
import org.maplibre.android.offline.OfflineRegion

data class MapLibreState(
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val regionsState: UiState<List<OfflineRegion>> = UiState.Loading,
    val downloadState: UiState<Unit> = UiState.Loading,
    val locationState: LocationState = LocationState(),
    val showRegionSuggestion: Boolean = false,
    val suggestedRegionName: String? = null,
    var isFabExpanded: Boolean = false
) {
    val showDownloadScreen: Boolean
        get() = isDownloading
}
