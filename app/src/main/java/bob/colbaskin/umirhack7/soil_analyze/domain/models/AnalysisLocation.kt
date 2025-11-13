package bob.colbaskin.umirhack7.soil_analyze.domain.models

data class AnalysisLocation(
    val type: String = "Point",
    val coordinates: List<String> = emptyList()
)
