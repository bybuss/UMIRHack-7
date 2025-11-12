package bob.colbaskin.umirhack7.maplibre.domain.models

data class Zone(
    val id: Int,
    val name: String,
    val area: Double,
    val color: String,
    val geometry: Geometry,
)