package bob.colbaskin.umirhack7.maplibre.utils

import android.util.Log
import org.maplibre.android.offline.OfflineRegion

private const val TAG = "MapLibre"

fun OfflineRegion.getReadableInfo(): String {
    return try {
        val metadata = String(this.metadata)
        "ID: ${this.id}, Name: $metadata"
    } catch (e: Exception) {
        Log.e(TAG, e.message.toString())
        "ID: ${this.id}, Name: Unknown"
    }
}
