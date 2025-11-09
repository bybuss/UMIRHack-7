package bob.colbaskin.umirhack7.maplibre.presentation

sealed interface MapLibreAction {
    data object LoadOfflineRegions : MapLibreAction
    data object DownloadCurrentRegion : MapLibreAction
    data object CancelDownload : MapLibreAction
    data object ClearError : MapLibreAction
    data object RequestLocationPermission : MapLibreAction
    data object GetCurrentLocation : MapLibreAction
    data object DismissRegionSuggestion : MapLibreAction
    data class DeleteRegion(val regionId: Long) : MapLibreAction
    data object ToggleFabExpand : MapLibreAction
    data object CloseFabMenu : MapLibreAction
}
