package bob.colbaskin.umirhack7.point_picker.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.point_picker.domain.GetZoneByIdUseCase
import bob.colbaskin.umirhack7.point_picker.domain.PointInPolygonChecker
import bob.colbaskin.umirhack7.point_picker.domain.model.MeasurementPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import javax.inject.Inject

@HiltViewModel
class PointPickerViewModel @Inject constructor(
    private val getZoneByIdUseCase: GetZoneByIdUseCase,
    private val pointInPolygonChecker: PointInPolygonChecker
) : ViewModel() {

    var state by mutableStateOf(PointPickerState())
        private set

    private var validationJob: Job? = null

    fun onAction(action: PointPickerAction) {
        when (action) {
            is PointPickerAction.LoadZoneData -> loadZoneData(action.zoneId)
            is PointPickerAction.UpdatePointPosition -> updatePointPositionWithDebounce(action.latLng)
            PointPickerAction.UseCurrentLocation -> useCurrentLocation()
        }
    }

    private fun updatePointPositionWithDebounce(latLng: LatLng) {
        validationJob?.cancel()

        state = state.copy(
            measurementPoint = MeasurementPoint(
                coordinates = latLng,
                isValid = state.measurementPoint.isValid
            )
        )

        validationJob = viewModelScope.launch {
            delay(500)
            validatePointPosition(latLng)
        }
    }

    private fun validatePointPosition(latLng: LatLng) {
        val currentZone = state.zone
        if (currentZone != null) {
            val vertices = currentZone.geometry.toLatLngList()
            val isInside = pointInPolygonChecker.isPointInPolygon(latLng, vertices)

            state = state.copy(
                measurementPoint = MeasurementPoint(
                    coordinates = latLng,
                    isValid = isInside
                )
            )
        }
    }
    private fun loadZoneData(zoneId: Int) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            try {
                val zone = getZoneByIdUseCase(zoneId)
                val zoneCenter = calculateZoneCenter(zone)

                state = state.copy(
                    isLoading = false,
                    zone = zone,
                    cameraTarget = zoneCenter,
                    measurementPoint = MeasurementPoint(
                        coordinates = zoneCenter,
                        isValid = true
                    )
                )

                validatePointPosition(zoneCenter)
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Ошибка загрузки зоны: ${e.message}"
                )
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

    private fun useCurrentLocation() {
        val currentPoint = state.measurementPoint.coordinates
        if (currentPoint != null) {
            state = state.copy(cameraTarget = currentPoint)
        }
    }
}
