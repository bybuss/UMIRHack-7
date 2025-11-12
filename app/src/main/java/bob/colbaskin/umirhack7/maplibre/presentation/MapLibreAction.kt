package bob.colbaskin.umirhack7.maplibre.presentation

import bob.colbaskin.umirhack7.maplibre.domain.models.Field

sealed interface MapLibreAction {
    data object LoadOfflineRegions : MapLibreAction
    data object DownloadCurrentRegion : MapLibreAction
    data object CancelDownload : MapLibreAction
    data object ClearError : MapLibreAction
    data object GetCurrentLocation : MapLibreAction
    data object DismissRegionSuggestion : MapLibreAction
    data class DeleteRegion(val regionId: Long) : MapLibreAction
    data object ToggleFabExpand : MapLibreAction
    data object CloseFabMenu : MapLibreAction
    data object LoadFields : MapLibreAction
    data object ToggleFieldsVisibility : MapLibreAction
    data class SelectField(val field: Field) : MapLibreAction
    data object ClearSelectedField : MapLibreAction
    data class FieldClicked(val field: Field) : MapLibreAction
    data class UpdateSearchQuery(val query: String) : MapLibreAction
    data object PerformSearch : MapLibreAction
    data object ClearSearch : MapLibreAction
    data class NavigateToFieldDetails(val id: Int): MapLibreAction
}
