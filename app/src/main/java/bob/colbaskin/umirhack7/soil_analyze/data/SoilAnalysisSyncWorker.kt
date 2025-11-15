package bob.colbaskin.umirhack7.soil_analyze.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilAnalysisNotificationRepository
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SoilAnalysisSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val soilRepository: SoilRepository,
    private val notificationRepository: SoilAnalysisNotificationRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            when (val result = soilRepository.syncPendingReports()) {
                is ApiResult.Success -> {
                    notificationRepository.showSyncSuccess()
                    Result.success()
                }
                is ApiResult.Error -> {
                    notificationRepository.showSyncError(result.text)
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            notificationRepository.showSyncError(e.message ?: "Unknown error")
            Result.retry()
        }
    }
}
