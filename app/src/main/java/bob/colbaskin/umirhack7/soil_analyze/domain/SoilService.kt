package bob.colbaskin.umirhack7.soil_analyze.domain

import bob.colbaskin.umirhack7.soil_analyze.data.models.CreateReportBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface SoilService {

    @PUT("/api/ground/create")
    suspend fun createReport(
        @Header("") userId: String,
        @Body body: CreateReportBody
    ): Response<Unit>
}
