package bob.colbaskin.umirhack7.soil_analyze.domain

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData

interface SoilRepository {

    suspend fun createReport(soilData: SoilAnalysisData): ApiResult<Unit>
    suspend fun syncPendingReports(): ApiResult<Unit>
}
