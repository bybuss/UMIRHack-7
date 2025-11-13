package bob.colbaskin.umirhack7.soil_analyze.presentation

import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.Field

data class SoilAnalyzeState(
    val isSyncing: Boolean = false,
    val fieldDetailState: UiState<Field?> = UiState.Loading,
    val expandedZoneId: Int? = null,

    val zoneAnalysisStates: Map<Int, ZoneAnalysisState> = emptyMap()
) {
    fun getZoneAnalysisState(zoneId: Int): ZoneAnalysisState {
        return zoneAnalysisStates[zoneId] ?: ZoneAnalysisState()
    }

    fun updateZoneAnalysisState(zoneId: Int, newState: ZoneAnalysisState): SoilAnalyzeState {
        val updatedStates = zoneAnalysisStates.toMutableMap()
        updatedStates[zoneId] = newState
        return this.copy(zoneAnalysisStates = updatedStates)
    }
}
