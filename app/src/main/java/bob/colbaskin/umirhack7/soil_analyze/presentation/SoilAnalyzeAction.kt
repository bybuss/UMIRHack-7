package bob.colbaskin.umirhack7.soil_analyze.presentation

import org.maplibre.android.geometry.LatLng

sealed interface SoilAnalyzeAction {
    data class LoadFieldDetail(val fieldId: Int) : SoilAnalyzeAction
    data class SyncFieldDetail(val fieldId: Int) : SoilAnalyzeAction
    object ClearFieldDetail : SoilAnalyzeAction
    data class UpdateMeasurementPoint(val point: LatLng) : SoilAnalyzeAction
}
