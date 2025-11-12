package bob.colbaskin.umirhack7.maplibre.domain.fields

import bob.colbaskin.umirhack7.maplibre.data.models.FieldDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface FieldsService {

    @GET("/api/fields/list")
    suspend fun getFieldsList(): List<FieldDTO>

    @GET("/api/fields/detail/{fieldId}")
    suspend fun getFieldDetail(@Path("fieldId") fieldId: Int): List<FieldDTO>
}
