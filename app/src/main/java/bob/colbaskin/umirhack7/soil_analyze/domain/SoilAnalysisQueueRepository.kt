package bob.colbaskin.umirhack7.soil_analyze.domain

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.soil_analyze.data.local.entity.SoilAnalysisQueueEntity
import bob.colbaskin.umirhack7.soil_analyze.data.models.AnalysisQueueStatus
import kotlinx.coroutines.flow.Flow

interface SoilAnalysisQueueRepository {
    suspend fun queueAnalysis(analysis: SoilAnalysisQueueEntity): ApiResult<Unit>
    suspend fun getPendingAnalyses(): List<SoilAnalysisQueueEntity>
    suspend fun updateAnalysisStatus(analysisId: String, status: AnalysisQueueStatus, errorMessage: String? = null)
    fun getPendingCount(): Flow<Int>
    suspend fun cleanupOldAnalyses()
    suspend fun updateAnalysis(analysis: SoilAnalysisQueueEntity): ApiResult<Unit>
}
