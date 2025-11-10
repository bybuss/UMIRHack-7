package bob.colbaskin.umirhack7.maplibre.presentation.fields

import bob.colbaskin.umirhack7.maplibre.data.models.FieldDTO
import retrofit2.http.GET

interface FieldsService {

    @GET("/api/fields/list")
    suspend fun getFieldsList(): List<FieldDTO>
}