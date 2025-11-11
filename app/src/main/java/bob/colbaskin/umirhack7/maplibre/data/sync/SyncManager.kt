package bob.colbaskin.umirhack7.maplibre.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val SYNC_WORK_NAME = "fields_sync"
        private const val TAG = "MapLibre"
        private const val SYNC_HOURS: Long = 4
    }

    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<FieldsSyncWorker>(
                repeatInterval = SYNC_HOURS,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )

        Log.d(TAG, "SyncManager: Scheduled periodic sync every $SYNC_HOURS hours")
    }

    fun scheduleOneTimeSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<FieldsSyncWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncWorkRequest
        )

        Log.d(TAG, "SyncManager: Scheduled one-time sync")
    }

    fun cancelSync() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        Log.d(TAG, "SyncManager: Cancelled sync")
    }
}
