package bob.colbaskin.umirhack7.maplibre.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ZoneDTO(
    val id: Int,
    val name: String,
    val area: Double,
    val color: String,
    val geometry: GeometryDTO,
)
