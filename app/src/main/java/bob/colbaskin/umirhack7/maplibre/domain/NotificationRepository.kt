package bob.colbaskin.umirhack7.maplibre.domain

interface NotificationRepository {
    suspend fun startDownloadService(regionName: String)
    suspend fun updateDownloadProgress(regionName: String, progress: Int)
    suspend fun completeDownload(regionName: String)
    suspend fun showDownloadError(regionName: String, error: String)
    suspend fun cancelDownloadNotification()
    suspend fun requestNotificationPermission()
}