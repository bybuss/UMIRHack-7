package bob.colbaskin.umirhack7.maplibre.presentation.fields

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.maplibre.domain.models.Field

interface FieldsRepository {

    suspend fun getFieldsList(): ApiResult<List<Field>>
}