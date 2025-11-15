package bob.colbaskin.umirhack7.soil_analyze.data

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.soil_analyze.data.local.SoilAnalysisDatabase
import bob.colbaskin.umirhack7.soil_analyze.data.local.entity.SoilAnalysisQueueEntity
import bob.colbaskin.umirhack7.soil_analyze.data.models.AnalysisQueueStatus
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilAnalysisQueueRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Calendar
import javax.inject.Inject

class SoilAnalysisQueueRepositoryImpl @Inject constructor(
    private val database: SoilAnalysisDatabase
) : SoilAnalysisQueueRepository {

    private val dao = database.soilAnalysisQueueDao()

    override suspend fun queueAnalysis(analysis: SoilAnalysisQueueEntity): ApiResult<Unit> {
        return try {
            dao.insert(analysis)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(title = "Failed to queue analysis: ${e.message}", text = "SoilAnalysisQueueRepositoryImplError")
        }
    }

    override suspend fun getPendingAnalyses(): List<SoilAnalysisQueueEntity> {
        val pending = dao.getByStatus(AnalysisQueueStatus.PENDING)
        val failed = dao.getByStatus(AnalysisQueueStatus.FAILED)
        return pending + failed
    }

    override suspend fun updateAnalysisStatus(analysisId: String, status: AnalysisQueueStatus, errorMessage: String?) {
        val analysis = dao.getById(analysisId) ?: return
        val updatedAnalysis = analysis.copy(
            status = status,
            errorMessage = errorMessage,
            attemptCount = analysis.attemptCount + 1,
            lastAttemptDate = if (status == AnalysisQueueStatus.FAILED) Date() else analysis.lastAttemptDate
        )
        dao.update(updatedAnalysis)
    }

    override fun getPendingCount(): Flow<Int> {
        return dao.getPendingCount()
    }

    override suspend fun cleanupOldAnalyses() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        dao.deleteOldSuccess(calendar.time)
    }

    override suspend fun updateAnalysis(analysis: SoilAnalysisQueueEntity): ApiResult<Unit> {
        return try {
            dao.update(analysis)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error("Failed to update analysis: ${e.message}", "")
        }
    }
}
