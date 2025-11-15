package bob.colbaskin.umirhack7.soil_analyze.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateReportBody(
    val N: Double,
    val P: Double,
    val K: Double,
    val Temperature: Double,
    val Humidity: Double,
    val pH: Double,
    val RainFall: Double,
    val createdAt: String,
    val location: Location
)

@Serializable
data class Location(
    val type: String,
    val coordinates: List<String>
)