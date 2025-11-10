package bob.colbaskin.umirhack7.maplibre.data.models

import kotlinx.serialization.Serializable

@Serializable
data class GeometryDTO(
    val type: String,
    val coordinates: List<List<List<Double>>>
)
