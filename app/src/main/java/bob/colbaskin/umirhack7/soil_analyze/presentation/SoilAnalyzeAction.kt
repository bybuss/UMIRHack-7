package bob.colbaskin.umirhack7.soil_analyze.presentation

sealed interface SoilAnalyzeAction {
    data class LoadFieldDetail(val fieldId: Int) : SoilAnalyzeAction
    data class SyncFieldDetail(val fieldId: Int) : SoilAnalyzeAction
    object ClearFieldDetail : SoilAnalyzeAction
}
