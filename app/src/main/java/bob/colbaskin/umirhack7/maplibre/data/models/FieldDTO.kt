package bob.colbaskin.umirhack7.maplibre.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FieldDTO(
    val id: Int,
    val name: String,
    val area: Double,
    val color: String,
    val geometry: GeometryDTO,
    val zones: List<ZoneDTO>
)
