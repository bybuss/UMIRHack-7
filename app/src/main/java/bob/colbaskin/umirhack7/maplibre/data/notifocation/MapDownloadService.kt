package bob.colbaskin.umirhack7.maplibre.data.notifocation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import bob.colbaskin.umirhack7.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapDownloadService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    private var currentRegionName = ""
    private var isCancelled = false
    private var broadcastSent = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                if (isCancelled) return START_NOT_STICKY
                val regionName = intent.getStringExtra(EXTRA_REGION_NAME) ?: return START_NOT_STICKY
                currentRegionName = regionName
                isCancelled = false
                broadcastSent = false
                startForegroundService(regionName)
            }
            ACTION_UPDATE_PROGRESS -> {
                if (isCancelled) return START_NOT_STICKY
                val progress = intent.getIntExtra(EXTRA_PROGRESS, 0)
                val regionName = intent.getStringExtra(EXTRA_REGION_NAME) ?: currentRegionName
                updateProgressNotification(regionName, progress)
            }
            ACTION_COMPLETE -> {
                val regionName = intent.getStringExtra(EXTRA_REGION_NAME) ?: currentRegionName
                showCompletionNotification(regionName)
                stopForeground(true)
                stopSelf()
            }
            ACTION_ERROR -> {
                val regionName = intent.getStringExtra(EXTRA_REGION_NAME) ?: currentRegionName
                val errorDescription = intent.getStringExtra(EXTRA_ERROR_DESCRIPTION) ?: "Произошла ошибка при загрузке"
                showErrorNotification(regionName, errorDescription)
                stopForeground(true)
                stopSelf()
            }
            ACTION_CANCEL -> {
                if (!isCancelled) {
                    isCancelled = true
                    sendDownloadCancelledBroadcast()
                    stopForeground(true)
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun sendDownloadCancelledBroadcast() {
        if (broadcastSent) return

        broadcastSent = true
        val intent = Intent(ACTION_DOWNLOAD_CANCELLED).apply {
            putExtra(EXTRA_REGION_NAME, currentRegionName)
            `package` = this@MapDownloadService.packageName
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendBroadcast(intent, null)
        } else {
            sendBroadcast(intent)
        }
    }

    private fun startForegroundService(regionName: String) {
        val notification = buildProgressNotification(
            title = "Загрузка региона $regionName",
            text = "Подготовка к загрузке...",
            progress = 0,
            indeterminate = true,
            regionName = regionName
        )
        startForeground(PROGRESS_NOTIFICATION_ID, notification)
    }

    private fun updateProgressNotification(regionName: String, progress: Int) {
        val text = when {
            progress == 0 -> "Начинаем загрузку..."
            progress < 25 -> "Загружаем базовые данные..."
            progress < 50 -> "Загружаем детали карты..."
            progress < 75 -> "Загружаем высокодетализированные данные..."
            progress < 100 -> "Завершаем загрузку..."
            else -> "Финальная обработка..."
        }

        val notification = buildProgressNotification(
            title = "Загрузка региона $regionName.\nКарта может не прогружаться на время загрузки!",
            text = "$text ($progress%)",
            progress = progress,
            indeterminate = false,
            regionName = regionName
        )

        startForeground(PROGRESS_NOTIFICATION_ID, notification)
    }

    private fun showCompletionNotification(regionName: String) {
        notificationManager.cancel(PROGRESS_NOTIFICATION_ID)

        val notification = buildResultNotification(
            title = "Загрузка завершена ✅",
            text = "Регион $regionName готов к использованию в оффлайн-режиме",
            channelId = COMPLETION_CHANNEL_ID
        )
        notificationManager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun showErrorNotification(regionName: String, errorDescription: String) {
        notificationManager.cancel(PROGRESS_NOTIFICATION_ID)

        val notification = buildResultNotification(
            title = "Ошибка загрузки ❌",
            text = "Не удалось загрузить регион $regionName. $errorDescription",
            channelId = ERROR_CHANNEL_ID
        )
        notificationManager.notify(ERROR_NOTIFICATION_ID, notification)
    }

    private fun buildProgressNotification(
        title: String,
        text: String,
        progress: Int,
        indeterminate: Boolean,
        regionName: String
    ): Notification {
        val cancelIntent = Intent(this, MapDownloadService::class.java).apply {
            action = ACTION_CANCEL
            putExtra(EXTRA_REGION_NAME, regionName)
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            CANCEL_REQUEST_CODE,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, PROGRESS_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.plant)
            .setProgress(100, progress, indeterminate)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .setAutoCancel(false)
            .addAction(
                R.drawable.ic_cancel,
                "Отменить",
                cancelPendingIntent
            )
            .build()
    }

    private fun buildResultNotification(
        title: String,
        text: String,
        channelId: String
    ): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.plant)
            .setOngoing(false)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_STATUS)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val progressChannel = NotificationChannel(
                PROGRESS_CHANNEL_ID,
                PROGRESS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Уведомления о прогрессе загрузки оффлайн-карт"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(null, null)
            }

            val completionChannel = NotificationChannel(
                COMPLETION_CHANNEL_ID,
                COMPLETION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления об успешной загрузке карт"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val errorChannel = NotificationChannel(
                ERROR_CHANNEL_ID,
                ERROR_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления об ошибках загрузки карт"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(progressChannel)
            notificationManager.createNotificationChannel(completionChannel)
            notificationManager.createNotificationChannel(errorChannel)
        }
    }

    companion object {
        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"
        const val ACTION_UPDATE_PROGRESS = "ACTION_UPDATE_PROGRESS"
        const val ACTION_COMPLETE = "ACTION_COMPLETE"
        const val ACTION_ERROR = "ACTION_ERROR"
        const val ACTION_CANCEL = "ACTION_CANCEL"
        const val ACTION_DOWNLOAD_CANCELLED = "ACTION_DOWNLOAD_CANCELLED"

        const val EXTRA_REGION_NAME = "EXTRA_REGION_NAME"
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_ERROR_DESCRIPTION = "EXTRA_ERROR_DESCRIPTION"

        const val PROGRESS_NOTIFICATION_ID = 1001
        const val COMPLETION_NOTIFICATION_ID = 1002
        const val ERROR_NOTIFICATION_ID = 1003

        const val PROGRESS_CHANNEL_ID = "map_download_progress_channel"
        const val COMPLETION_CHANNEL_ID = "map_download_completion_channel"
        const val ERROR_CHANNEL_ID = "map_download_error_channel"

        const val PROGRESS_CHANNEL_NAME = "Прогресс загрузки"
        const val COMPLETION_CHANNEL_NAME = "Завершение загрузки"
        const val ERROR_CHANNEL_NAME = "Ошибки загрузки"

        const val CANCEL_REQUEST_CODE = 1001
    }
}
