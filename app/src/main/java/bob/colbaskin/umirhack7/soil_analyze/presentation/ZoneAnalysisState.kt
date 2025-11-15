package bob.colbaskin.umirhack7.soil_analyze.presentation

import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import org.maplibre.android.geometry.LatLng

data class ZoneAnalysisState(
    val soilAnalysisData: SoilAnalysisData = SoilAnalysisData(),
    val measurementPoint: LatLng? = null,
    val locationError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false,
    val showLocationOptions: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap()
)
