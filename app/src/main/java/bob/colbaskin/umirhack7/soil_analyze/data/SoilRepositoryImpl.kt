package bob.colbaskin.umirhack7.soil_analyze.data

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import bob.colbaskin.umirhack7.soil_analyze.data.models.CreateReportBody
import bob.colbaskin.umirhack7.soil_analyze.data.models.toData
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilRepository
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilService
import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import javax.inject.Inject

class SoilRepositoryImpl @Inject constructor(
    private val soilApi: SoilService
): SoilRepository {
    override suspend fun createReport(soilData: SoilAnalysisData): ApiResult<Unit> {
        return safeApiCall<Unit, Unit>(
            apiCall = {
                soilApi.createReport(
                    userId = "8b41db6e-af65-5e01-4ac0-4d706875b964",
                    body = CreateReportBody(
                        N = soilData.N,
                        P = soilData.P,
                        K = soilData.K,
                        Temperature = soilData.Temperature,
                        Humidity = soilData.Humidity,
                        pH = soilData.pH,
                        RainFall = soilData.RainFall,
                        createdAt = soilData.createdAt.toString(), // сделать нрмальное преобразование из Date в формат 2025-11-14T22:08:43.024Z
                        location = soilData.location!!.toData()
                    )
                )
            },
            successHandler = { response ->
                response
            }
        )
    }
}
