package bob.colbaskin.umirhack7.soil_analyze.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsRepository
import bob.colbaskin.umirhack7.point_picker.domain.PointInPolygonChecker
import bob.colbaskin.umirhack7.soil_analyze.data.models.toAnalysisLocation
import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import bob.colbaskin.umirhack7.soil_analyze.utils.LocationClient
import bob.colbaskin.umirhack7.soil_analyze.utils.SoilAnalysisValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import javax.inject.Inject

private const val TAG = "Soil"

@HiltViewModel
class SoilAnalyzeViewModel @Inject constructor(
    private val fieldsRepository: FieldsRepository,
    private val locationClient: LocationClient,
    private val pointInPolygonChecker: PointInPolygonChecker,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var state by mutableStateOf(SoilAnalyzeState())
        private set

    fun onAction(action: SoilAnalyzeAction) {
        when (action) {
            is SoilAnalyzeAction.LoadFieldDetail -> loadFieldDetail(action.fieldId)
            is SoilAnalyzeAction.SyncFieldDetail -> syncFieldDetail(action.fieldId)
            SoilAnalyzeAction.ClearFieldDetail -> clearFieldDetail()
            is SoilAnalyzeAction.ToggleZoneExpansion -> toggleZoneExpansion(action.zoneId)
            is SoilAnalyzeAction.UpdateZoneSoilAnalysisData -> updateZoneSoilAnalysisData(action.zoneId, action.data)
            is SoilAnalyzeAction.UpdateZoneMeasurementPoint -> updateZoneMeasurementPoint(action.zoneId, action.point)
            is SoilAnalyzeAction.SubmitZoneAnalysis -> submitZoneAnalysis(action.zoneId)
            is SoilAnalyzeAction.ShowZoneLocationOptions -> showZoneLocationOptions(action.zoneId)
            is SoilAnalyzeAction.HideZoneLocationOptions -> hideZoneLocationOptions(action.zoneId)
            is SoilAnalyzeAction.UseCurrentLocationForZone -> useCurrentLocationForZone(action.zoneId)
            is SoilAnalyzeAction.OpenMapForZone -> openMapForZone(action.zoneId)
            is SoilAnalyzeAction.ClearZoneLocationError -> clearZoneLocationError(action.zoneId)
        }
    }

    private fun updateZoneSoilAnalysisData(zoneId: Int, data: SoilAnalysisData) {
        Log.d(TAG, "updateZoneSoilAnalysisData: Zone $zoneId - $data")
        val currentState = state.getZoneAnalysisState(zoneId)

        val validationResult = SoilAnalysisValidator.validate(data)

        state = state.updateZoneAnalysisState(
            zoneId,
            currentState.copy(
                soilAnalysisData = data,
                validationErrors = validationResult.errors
            )
        )
    }

    private fun updateZoneMeasurementPoint(zoneId: Int, point: LatLng) {
        Log.d(TAG, "updateZoneMeasurementPoint: Zone $zoneId - $point")
        val currentState = state.getZoneAnalysisState(zoneId)
        state = state.updateZoneAnalysisState(
            zoneId,
            currentState.copy(
                measurementPoint = point,
                locationError = null
            )
        )
    }

    private fun submitZoneAnalysis(zoneId: Int) {
        Log.d(TAG, "submitZoneAnalysis: Submitting analysis for zone $zoneId")

        val zoneState = state.getZoneAnalysisState(zoneId)

        if (zoneState.measurementPoint == null) {
            val updatedState = zoneState.copy(submitError = "Необходимо указать местоположение анализа")
            state = state.updateZoneAnalysisState(zoneId, updatedState)
            return
        }

        val validationResult = SoilAnalysisValidator.validate(zoneState.soilAnalysisData)
        if (!validationResult.isValid) {
            val updatedState = zoneState.copy(
                validationErrors = validationResult.errors,
                submitError = "Пожалуйста, исправьте ошибки в форме"
            )
            state = state.updateZoneAnalysisState(zoneId, updatedState)
            return
        }

        val updatedState = zoneState.copy(
            isSubmitting = true,
            submitError = null,
            validationErrors = emptyMap()
        )
        state = state.updateZoneAnalysisState(zoneId, updatedState)

        viewModelScope.launch {
            try {
                val analysisData = zoneState.soilAnalysisData.copy(
                    location = zoneState.measurementPoint.toAnalysisLocation()
                )
                val userId = userPreferencesRepository.getUser().first().userId

                Log.d(TAG, "submitZoneAnalysis: Data prepared for userId=$userId, zone $zoneId - $analysisData")

                kotlinx.coroutines.delay(1000)

                val successState = zoneState.copy(
                    isSubmitting = false,
                    submitSuccess = true,
                    submitError = null,
                    validationErrors = emptyMap()
                )
                state = state.updateZoneAnalysisState(zoneId, successState)

                Log.d(TAG, "submitZoneAnalysis: Analysis submitted successfully for zone $zoneId")

                kotlinx.coroutines.delay(500)
                state = state.copy(expandedZoneId = null)
                state = state.updateZoneAnalysisState(zoneId, ZoneAnalysisState(
                    soilAnalysisData = SoilAnalysisData(),
                    measurementPoint = null,
                    submitSuccess = true
                ))

            } catch (e: Exception) {
                Log.e(TAG, "submitZoneAnalysis: Error submitting analysis for zone $zoneId", e)
                val errorState = zoneState.copy(
                    isSubmitting = false,
                    submitError = "Ошибка отправки данных: ${e.message}"
                )
                state = state.updateZoneAnalysisState(zoneId, errorState)
            }
        }
    }

    private fun showZoneLocationOptions(zoneId: Int) {
        Log.d(TAG, "showZoneLocationOptions: Zone $zoneId")
        val currentState = state.getZoneAnalysisState(zoneId)
        state = state.updateZoneAnalysisState(zoneId, currentState.copy(showLocationOptions = true))
    }

    private fun hideZoneLocationOptions(zoneId: Int) {
        Log.d(TAG, "hideZoneLocationOptions: Zone $zoneId")
        val currentState = state.getZoneAnalysisState(zoneId)
        state = state.updateZoneAnalysisState(zoneId, currentState.copy(showLocationOptions = false))
    }

    private fun useCurrentLocationForZone(zoneId: Int) {
        Log.d(TAG, "useCurrentLocationForZone: Zone $zoneId")

        val field = (state.fieldDetailState as? UiState.Success)?.data
        val zone = field?.zones?.find { it.id == zoneId }

        if (zone == null) {
            val currentState = state.getZoneAnalysisState(zoneId)
            state = state.updateZoneAnalysisState(
                zoneId,
                currentState.copy(locationError = "Зона не найдена")
            )
            return
        }

        if (!locationClient.hasLocationPermission()) {
            val currentState = state.getZoneAnalysisState(zoneId)
            state = state.updateZoneAnalysisState(
                zoneId,
                currentState.copy(
                    locationError = "Для использования текущего местоположения необходимы разрешения на доступ к геолокации"
                )
            )
            hideZoneLocationOptions(zoneId)
            return
        }

        viewModelScope.launch {
            try {
                val currentLocation = locationClient.getCurrentLocation()
                if (currentLocation != null) {
                    val point = LatLng(currentLocation.latitude, currentLocation.longitude)
                    val vertices = zone.geometry.toLatLngList()
                    val isInside = pointInPolygonChecker.isPointInPolygon(point, vertices)

                    if (isInside) {
                        updateZoneMeasurementPoint(zoneId, point)
                        Log.d(TAG, "useCurrentLocationForZone: Successfully set current location inside zone $zoneId")
                    } else {
                        val currentState = state.getZoneAnalysisState(zoneId)
                        state = state.updateZoneAnalysisState(
                            zoneId,
                            currentState.copy(
                                locationError = "Ваше текущее местоположение находится вне зоны. Пожалуйста, выберите точку на карте."
                            )
                        )
                        Log.d(TAG, "useCurrentLocationForZone: Current location is outside zone $zoneId")
                    }
                } else {
                    val currentState = state.getZoneAnalysisState(zoneId)
                    state = state.updateZoneAnalysisState(
                        zoneId,
                        currentState.copy(
                            locationError = "Не удалось получить текущее местоположение. Пожалуйста, выберите точку на карте."
                        )
                    )
                    Log.d(TAG, "useCurrentLocationForZone: Failed to get current location for zone $zoneId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "useCurrentLocationForZone: Error getting location for zone $zoneId", e)
                val currentState = state.getZoneAnalysisState(zoneId)
                state = state.updateZoneAnalysisState(
                    zoneId,
                    currentState.copy(
                        locationError = "Ошибка получения местоположения: ${e.message}. Пожалуйста, выберите точку на карте."
                    )
                )
            }
        }

        hideZoneLocationOptions(zoneId)
    }

    private fun openMapForZone(zoneId: Int) {
        Log.d(TAG, "openMapForZone: Opening map for zone $zoneId")
        hideZoneLocationOptions(zoneId)
        clearZoneLocationError(zoneId)
    }

    private fun clearZoneLocationError(zoneId: Int) {
        Log.d(TAG, "clearZoneLocationError: Zone $zoneId")
        val currentState = state.getZoneAnalysisState(zoneId)
        state = state.updateZoneAnalysisState(zoneId, currentState.copy(locationError = null))
    }

    private fun toggleZoneExpansion(zoneId: Int) {
        val currentExpandedZoneId = state.expandedZoneId
        val newExpandedZoneId = if (currentExpandedZoneId == zoneId) null else zoneId
        state = state.copy(expandedZoneId = newExpandedZoneId)
        Log.d(TAG, "toggleZoneExpansion: Zone $zoneId expanded: ${newExpandedZoneId == zoneId}")
    }

    private fun clearFieldDetail() {
        Log.d(TAG, "clearFieldDetail: Clearing field detail")
        state = state.copy(fieldDetailState = UiState.Loading)
    }

    private fun syncFieldDetail(fieldId: Int) {
        Log.d(TAG, "syncFieldDetail: Starting manual sync for field $fieldId")
        state = state.copy(fieldDetailState = UiState.Loading)

        viewModelScope.launch {
            when (val result = fieldsRepository.syncField(fieldId)) {
                is ApiResult.Success -> {
                    Log.d(TAG, "syncFieldDetail: Sync successful for field $fieldId")
                    loadFieldDetail(fieldId)
                }
                is ApiResult.Error -> {
                    Log.e(TAG, "syncFieldDetail: Sync failed for field $fieldId: ${result.text}")
                    state = when (val dbResult = fieldsRepository.getFieldFromDatabase(fieldId)) {
                        is ApiResult.Success -> {
                            state.copy(fieldDetailState = UiState.Success(dbResult.data))
                        }
                        is ApiResult.Error -> {
                            state.copy(
                                fieldDetailState = UiState.Error(
                                    title = "Ошибка загрузки поля",
                                    text = "Не удалось загрузить данные поля. Проверьте подключение к интернету."
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadFieldDetail(fieldId: Int) {
        Log.d(TAG, "loadFieldDetail: Loading field detail for fieldId: $fieldId")

        viewModelScope.launch {
            state = state.copy(fieldDetailState = UiState.Loading)

            when (val dbResult = fieldsRepository.getFieldFromDatabase(fieldId)) {
                is ApiResult.Success -> {
                    if (dbResult.data != null) {
                        Log.d(TAG, "loadFieldDetail: Successfully loaded field ${dbResult.data.name} from database")
                        state = state.copy(fieldDetailState = UiState.Success(dbResult.data))
                        syncFieldDetailInBackground(fieldId)
                    } else {
                        Log.d(TAG, "loadFieldDetail: Field not found in database, syncing from server")
                        syncFieldDetail(fieldId)
                    }
                }
                is ApiResult.Error -> {
                    Log.e(TAG, "loadFieldDetail: Error loading from database: ${dbResult.text}")
                    syncFieldDetail(fieldId)
                }
            }
        }
    }

    private fun syncFieldDetailInBackground(fieldId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "syncFieldDetailInBackground: Starting background sync for field $fieldId")
            try {
                val syncResult = fieldsRepository.syncField(fieldId)
                Log.d(TAG, "syncFieldDetailInBackground: Sync result for field $fieldId: $syncResult")
            } catch (e: Exception) {
                Log.e(TAG, "syncFieldDetailInBackground: Error during field sync", e)
            }
        }
    }
}
