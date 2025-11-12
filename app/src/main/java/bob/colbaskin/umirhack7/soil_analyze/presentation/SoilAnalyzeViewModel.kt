package bob.colbaskin.umirhack7.soil_analyze.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Soil"

@HiltViewModel
class SoilAnalyzeViewModel @Inject constructor(
    private val fieldsRepository: FieldsRepository
): ViewModel() {
    var state by mutableStateOf(SoilAnalyzeState())
        private set

    fun onAction(action: SoilAnalyzeAction) {
        when (action) {
            SoilAnalyzeAction.ClearFieldDetail -> clearFieldDetail()
            is SoilAnalyzeAction.LoadFieldDetail -> loadFieldDetail(action.fieldId)
            is SoilAnalyzeAction.SyncFieldDetail -> syncFieldDetail(action.fieldId)
        }
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
