package bob.colbaskin.umirhack7.point_picker.presentation

import org.maplibre.android.geometry.LatLng

sealed interface PointPickerAction {
    data class LoadZoneData(val zoneId: Int) : PointPickerAction
    data class UpdatePointPosition(val latLng: LatLng) : PointPickerAction
    object ConfirmSelection : PointPickerAction
    object CancelSelection : PointPickerAction
    object UseCurrentLocation : PointPickerAction
}
