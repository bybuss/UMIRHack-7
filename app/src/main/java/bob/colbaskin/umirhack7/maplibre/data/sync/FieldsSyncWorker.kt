package bob.colbaskin.umirhack7.maplibre.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "MapLibre"

class FieldsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val fieldsRepository: FieldsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "FieldsSyncWorker: Starting sync work")
        try {
            Log.d(TAG, "FieldsSyncWorker: Calling fieldsRepository.syncFields()")
            val result = fieldsRepository.syncFields()
            if (result is bob.colbaskin.umirhack7.common.ApiResult.Success) {
                Log.d(TAG, "FieldsSyncWorker: Sync completed successfully")
                Result.success()
            } else {
                Log.e(TAG, "FieldsSyncWorker: Sync failed with error")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "FieldsSyncWorker: Exception during sync: ${e.message}")
            Result.retry()
        }
    }
}
