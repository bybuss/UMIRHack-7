package bob.colbaskin.umirhack7.soil_analyze.domain

interface SoilAnalysisNotificationRepository {
    suspend fun showQueuedNotification(zoneName: String)
    suspend fun showSyncSuccess()
    suspend fun showSyncError(error: String)
    suspend fun showSyncProgress(pendingCount: Int)
}