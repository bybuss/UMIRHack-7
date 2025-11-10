package bob.colbaskin.umirhack7.maplibre.presentation

import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.domain.models.LocationState
import org.maplibre.android.geometry.LatLng
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
    val isFabExpanded: Boolean = false,
    val currentDownloadRegionId: Long? = null,
    val fieldsState: UiState<List<Field>> = UiState.Loading,
    val showFields: Boolean = true,
    val selectedField: Field? = null,
    val cameraTarget: LatLng? = null
)
