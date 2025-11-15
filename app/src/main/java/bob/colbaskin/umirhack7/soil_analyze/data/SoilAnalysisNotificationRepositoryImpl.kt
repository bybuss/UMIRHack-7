package bob.colbaskin.umirhack7.soil_analyze.data

import android.content.Context
import android.content.Intent
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilAnalysisNotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SoilAnalysisNotificationRepositoryImpl @Inject constructor(
    private val context: Context
) : SoilAnalysisNotificationRepository {

    override suspend fun showQueuedNotification(zoneName: String) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, SoilAnalysisNotificationService::class.java).apply {
                action = SoilAnalysisNotificationService.ACTION_ANALYSIS_QUEUED
                putExtra(SoilAnalysisNotificationService.EXTRA_ZONE_NAME, zoneName)
            }
            startService(intent)
        }
    }

    override suspend fun showSyncSuccess() {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, SoilAnalysisNotificationService::class.java).apply {
                action = SoilAnalysisNotificationService.ACTION_SYNC_SUCCESS
            }
            startService(intent)
        }
    }

    override suspend fun showSyncError(error: String) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, SoilAnalysisNotificationService::class.java).apply {
                action = SoilAnalysisNotificationService.ACTION_SYNC_ERROR
                putExtra(SoilAnalysisNotificationService.EXTRA_ERROR_MESSAGE, error)
            }
            startService(intent)
        }
    }

    override suspend fun showSyncProgress(pendingCount: Int) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, SoilAnalysisNotificationService::class.java).apply {
                action = SoilAnalysisNotificationService.ACTION_SYNC_PROGRESS
                putExtra(SoilAnalysisNotificationService.EXTRA_PENDING_COUNT, pendingCount)
            }
            startService(intent)
        }
    }

    private fun startService(intent: Intent) {
        context.startService(intent)
    }
}
