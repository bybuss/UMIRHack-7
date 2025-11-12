package bob.colbaskin.umirhack7.soil_analyze.presentation

import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.Field

data class SoilAnalyzeState (
    val isSyncing: Boolean = false,
    val fieldDetailState: UiState<Field?> = UiState.Loading,
)