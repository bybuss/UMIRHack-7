package bob.colbaskin.umirhack7.maplibre.data.notifocation

import android.content.Context
import android.content.Intent
import android.os.Build
import bob.colbaskin.umirhack7.maplibre.domain.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val context: Context
) : NotificationRepository {

    override suspend fun startDownloadService(regionName: String) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, MapDownloadService::class.java).apply {
                action = MapDownloadService.ACTION_START_DOWNLOAD
                putExtra(MapDownloadService.EXTRA_REGION_NAME, regionName)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override suspend fun updateDownloadProgress(regionName: String, progress: Int) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, MapDownloadService::class.java).apply {
                action = MapDownloadService.ACTION_UPDATE_PROGRESS
                putExtra(MapDownloadService.EXTRA_REGION_NAME, regionName)
                putExtra(MapDownloadService.EXTRA_PROGRESS, progress)
            }
            context.startService(intent)
        }
    }

    override suspend fun completeDownload(regionName: String) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, MapDownloadService::class.java).apply {
                action = MapDownloadService.ACTION_COMPLETE
                putExtra(MapDownloadService.EXTRA_REGION_NAME, regionName)
            }
            context.startService(intent)
        }
    }

    override suspend fun showDownloadError(regionName: String, errorDescription: String) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, MapDownloadService::class.java).apply {
                action = MapDownloadService.ACTION_ERROR
                putExtra(MapDownloadService.EXTRA_REGION_NAME, regionName)
                putExtra(MapDownloadService.EXTRA_ERROR_DESCRIPTION, errorDescription)
            }
            context.startService(intent)
        }
    }

    override suspend fun cancelDownloadNotification() {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, MapDownloadService::class.java).apply {
                action = MapDownloadService.ACTION_CANCEL
            }
            context.startService(intent)
        }
    }

    override suspend fun requestNotificationPermission() {}
}