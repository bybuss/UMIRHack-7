package bob.colbaskin.umirhack7.maplibre.data.fields

import android.util.Log
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import bob.colbaskin.umirhack7.maplibre.data.models.FieldDTO
import bob.colbaskin.umirhack7.maplibre.data.models.toDomain
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.presentation.fields.FieldsRepository
import bob.colbaskin.umirhack7.maplibre.presentation.fields.FieldsService
import javax.inject.Inject

private const val TAG = "MapLibre"

class FieldsRepositoryImpl @Inject constructor(
    private val fieldsApi: FieldsService
): FieldsRepository {
    override suspend fun getFieldsList(): ApiResult<List<Field>> {
        Log.d(TAG, "Attempting get fields")
        return safeApiCall<List<FieldDTO>, List<Field>>(
            apiCall = {
                fieldsApi.getFieldsList()
            },
            successHandler = { response ->
                Log.d(TAG, "Get fields successful: ${response.map { it.toDomain() }}")
                response.map { it.toDomain() }
            }
        )
    }
}
