package bob.colbaskin.umirhack7.maplibre.domain.models

data class Geometry(
    val type: String,
    val coordinates: List<List<List<Double>>>
)
