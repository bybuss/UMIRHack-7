package bob.colbaskin.umirhack7.point_picker.domain.model

import org.maplibre.android.geometry.LatLng

data class MeasurementPoint(
    val coordinates: LatLng? = null,
    val isValid: Boolean = false
)
