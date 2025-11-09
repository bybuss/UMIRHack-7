package bob.colbaskin.umirhack7.maplibre.presentation

sealed interface MapLibreAction {
    data object LoadOfflineRegions : MapLibreAction
    data object DownloadMoscowMap : MapLibreAction
    data object CancelDownload : MapLibreAction
    data object ClearError : MapLibreAction
    data class DeleteRegion(val regionId: Long) : MapLibreAction
}