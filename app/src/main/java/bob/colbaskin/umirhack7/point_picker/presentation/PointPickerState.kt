package bob.colbaskin.umirhack7.point_picker.presentation

import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.point_picker.domain.model.MeasurementPoint
import org.maplibre.android.geometry.LatLng


data class PointPickerState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val zone: Zone? = null,
    val measurementPoint: MeasurementPoint = MeasurementPoint(),
    val cameraTarget: LatLng? = null
)
