package bob.colbaskin.umirhack7.soil_analyze.presentation

import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import org.maplibre.android.geometry.LatLng

sealed interface SoilAnalyzeAction {
    data class LoadFieldDetail(val fieldId: Int) : SoilAnalyzeAction
    data class SyncFieldDetail(val fieldId: Int) : SoilAnalyzeAction
    object ClearFieldDetail : SoilAnalyzeAction
    data class ToggleZoneExpansion(val zoneId: Int) : SoilAnalyzeAction
    data class UpdateZoneSoilAnalysisData(val zoneId: Int, val data: SoilAnalysisData) : SoilAnalyzeAction
    data class UpdateZoneMeasurementPoint(val zoneId: Int, val point: LatLng) : SoilAnalyzeAction
    data class SubmitZoneAnalysis(val zoneId: Int) : SoilAnalyzeAction
    data class ShowZoneLocationOptions(val zoneId: Int) : SoilAnalyzeAction
    data class HideZoneLocationOptions(val zoneId: Int) : SoilAnalyzeAction
    data class UseCurrentLocationForZone(val zoneId: Int) : SoilAnalyzeAction
    data class OpenMapForZone(val zoneId: Int) : SoilAnalyzeAction
    data class ClearZoneLocationError(val zoneId: Int) : SoilAnalyzeAction
}
