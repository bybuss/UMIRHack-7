package bob.colbaskin.umirhack7.maplibre.domain.fields

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import kotlinx.coroutines.flow.Flow

interface FieldsRepository {

    fun getFieldsStream(): Flow<List<Field>>
    suspend fun syncFields(): ApiResult<Unit>
    suspend fun getFieldsList(): ApiResult<List<Field>>

    fun getFieldStream(fieldId: Int): Flow<Field?>
    suspend fun syncField(fieldId: Int): ApiResult<Unit>
    suspend fun getFieldFromDatabase(fieldId: Int): ApiResult<Field?>
}
