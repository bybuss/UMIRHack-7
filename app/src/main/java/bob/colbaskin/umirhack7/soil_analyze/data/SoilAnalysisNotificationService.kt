package bob.colbaskin.umirhack7.soil_analyze.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import bob.colbaskin.umirhack7.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SoilAnalysisNotificationService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ANALYSIS_QUEUED -> {
                val zoneName = intent.getStringExtra(EXTRA_ZONE_NAME) ?: "неизвестная зона"
                showQueuedNotification(zoneName)
            }
            ACTION_SYNC_SUCCESS -> {
                showSyncSuccessNotification()
            }
            ACTION_SYNC_ERROR -> {
                val error = intent.getStringExtra(EXTRA_ERROR_MESSAGE) ?: "Неизвестная ошибка"
                showSyncErrorNotification(error)
            }
            ACTION_SYNC_PROGRESS -> {
                val pendingCount = intent.getIntExtra(EXTRA_PENDING_COUNT, 0)
                showSyncProgressNotification(pendingCount)
            }
        }
        stopSelf()
        return START_NOT_STICKY
    }

    private fun showQueuedNotification(zoneName: String) {
        createNotificationChannels()

        val notification = NotificationCompat.Builder(this, SOIL_ANALYSIS_CHANNEL_ID)
            .setContentTitle("Анализ почвы поставлен в очередь")
            .setContentText("Анализ для зоны '$zoneName' будет отправлен при появлении интернета")
            .setSmallIcon(R.drawable.plant)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_STATUS)
            .build()

        notificationManager.notify(QUEUED_NOTIFICATION_ID, notification)
    }

    private fun showSyncSuccessNotification() {
        val notification = NotificationCompat.Builder(this, SOIL_ANALYSIS_CHANNEL_ID)
            .setContentTitle("Синхронизация завершена ✅")
            .setContentText("Все анализы почвы успешно отправлены на сервер")
            .setSmallIcon(R.drawable.plant)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_STATUS)
            .build()

        notificationManager.notify(SYNC_SUCCESS_NOTIFICATION_ID, notification)
    }

    private fun showSyncErrorNotification(error: String) {
        val notification = NotificationCompat.Builder(this, SOIL_ANALYSIS_CHANNEL_ID)
            .setContentTitle("Ошибка синхронизации ❌")
            .setContentText("Не удалось отправить анализы: $error")
            .setSmallIcon(R.drawable.plant)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_STATUS)
            .build()

        notificationManager.notify(SYNC_ERROR_NOTIFICATION_ID, notification)
    }

    private fun showSyncProgressNotification(pendingCount: Int) {
        val notification = NotificationCompat.Builder(this, SOIL_ANALYSIS_CHANNEL_ID)
            .setContentTitle("Синхронизация анализов")
            .setContentText("Отправляется $pendingCount анализов...")
            .setSmallIcon(R.drawable.plant)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .build()

        notificationManager.notify(SYNC_PROGRESS_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SOIL_ANALYSIS_CHANNEL_ID,
                SOIL_ANALYSIS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о статусе анализов почвы"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_ANALYSIS_QUEUED = "ACTION_ANALYSIS_QUEUED"
        const val ACTION_SYNC_SUCCESS = "ACTION_SYNC_SUCCESS"
        const val ACTION_SYNC_ERROR = "ACTION_SYNC_ERROR"
        const val ACTION_SYNC_PROGRESS = "ACTION_SYNC_PROGRESS"

        const val EXTRA_ZONE_NAME = "EXTRA_ZONE_NAME"
        const val EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE"
        const val EXTRA_PENDING_COUNT = "EXTRA_PENDING_COUNT"

        const val QUEUED_NOTIFICATION_ID = 2001
        const val SYNC_SUCCESS_NOTIFICATION_ID = 2002
        const val SYNC_ERROR_NOTIFICATION_ID = 2003
        const val SYNC_PROGRESS_NOTIFICATION_ID = 2004

        const val SOIL_ANALYSIS_CHANNEL_ID = "soil_analysis_channel"
        const val SOIL_ANALYSIS_CHANNEL_NAME = "Анализы почвы"
    }
}