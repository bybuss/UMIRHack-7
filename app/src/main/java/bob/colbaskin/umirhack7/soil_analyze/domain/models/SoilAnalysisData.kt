package bob.colbaskin.umirhack7.soil_analyze.domain.models

import java.util.Date

data class SoilAnalysisData(
    val N: Double = 0.0,
    val P: Double = 0.0,
    val K: Double = 0.0,
    val Temperature: Double = 0.0,
    val Humidity: Double = 0.0,
    val pH: Double = 0.0,
    val RainFall: Double = 0.0,
    val createdAt: Date = Date(),
    val location: AnalysisLocation? = null
)
