package bob.colbaskin.umirhack7.point_picker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.point_picker.domain.GetZoneByIdUseCase
import bob.colbaskin.umirhack7.point_picker.domain.PointInPolygonChecker
import bob.colbaskin.umirhack7.point_picker.domain.model.MeasurementPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import javax.inject.Inject

@HiltViewModel
class PointPickerViewModel @Inject constructor(
    private val getZoneByIdUseCase: GetZoneByIdUseCase,
    private val pointInPolygonChecker: PointInPolygonChecker
) : ViewModel() {

    private val _state = MutableStateFlow(PointPickerState())
    val state: StateFlow<PointPickerState> = _state.asStateFlow()

    fun onAction(action: PointPickerAction) {
        when (action) {
            is PointPickerAction.LoadZoneData -> loadZoneData(action.zoneId)
            is PointPickerAction.UpdatePointPosition -> updatePointPosition(action.latLng)
            PointPickerAction.ConfirmSelection -> confirmSelection()
            PointPickerAction.CancelSelection -> cancelSelection()
            PointPickerAction.UseCurrentLocation -> useCurrentLocation()
        }
    }

    private fun loadZoneData(zoneId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val zone = getZoneByIdUseCase(zoneId)
                val zoneCenter = calculateZoneCenter(zone)

                _state.update {
                    it.copy(
                        isLoading = false,
                        zone = zone,
                        cameraTarget = zoneCenter,
                        measurementPoint = MeasurementPoint(
                            coordinates = zoneCenter,
                            isValid = true
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки зоны: ${e.message}"
                    )
                }
            }
        }
    }

    private fun calculateZoneCenter(zone: Zone): LatLng {
        val vertices = zone.geometry.toLatLngList()
        if (vertices.isEmpty()) return LatLng(0.0, 0.0)

        var minLat = Double.MAX_VALUE
        var maxLat = -Double.MAX_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = -Double.MAX_VALUE

        vertices.forEach { latLng ->
            minLat = minOf(minLat, latLng.latitude)
            maxLat = maxOf(maxLat, latLng.latitude)
            minLon = minOf(minLon, latLng.longitude)
            maxLon = maxOf(maxLon, latLng.longitude)
        }

        return LatLng(
            (minLat + maxLat) / 2,
            (minLon + maxLon) / 2
        )
    }

    private fun updatePointPosition(latLng: LatLng) {
        val currentZone = _state.value.zone
        if (currentZone != null) {
            val vertices = currentZone.geometry.toLatLngList()
            val isInside = pointInPolygonChecker.isPointInPolygon(latLng, vertices)

            _state.update {
                it.copy(
                    measurementPoint = MeasurementPoint(
                        coordinates = latLng,
                        isValid = isInside
                    )
                )
            }
        }
    }

    private fun useCurrentLocation() {
        // TODO: Здесь будет получение текущего местоположения. Пока просто центрирую на текущей точечкеееее о-е, о-е, Солнышко Смоленское!
        val currentPoint = _state.value.measurementPoint.coordinates
        if (currentPoint != null) {
            _state.update {
                it.copy(cameraTarget = currentPoint)
            }
        }
    }

    private fun confirmSelection() {}

    private fun cancelSelection() {}
}
