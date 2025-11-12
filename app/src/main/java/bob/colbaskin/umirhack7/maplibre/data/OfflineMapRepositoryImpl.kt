package bob.colbaskin.umirhack7.maplibre.data

import android.util.Log
import bob.colbaskin.umirhack7.maplibre.domain.OfflineMapRepository
import jakarta.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineRegionStatus
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "MapLibre"

class OfflineMapRepositoryImpl @Inject constructor(
    private val offlineManager: OfflineManager
) : OfflineMapRepository {

    override suspend fun downloadRegion(
        styleUrl: String,
        bounds: LatLngBounds,
        minZoom: Double,
        maxZoom: Double,
        regionName: String
    ): OfflineRegion = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "Starting download region: $regionName")

        val definition = OfflineTilePyramidRegionDefinition(
            styleUrl,
            bounds,
            minZoom,
            maxZoom,
            1.0f
        )

        val metadata = regionName.toByteArray()

        offlineManager.createOfflineRegion(
            definition,
            metadata,
            object : OfflineManager.CreateOfflineRegionCallback {
                override fun onCreate(offlineRegion: OfflineRegion) {
                    Log.d(TAG, "Offline region created, starting download")
                    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
                    continuation.resume(offlineRegion)
                }

                override fun onError(error: String) {
                    Log.e(TAG, "Error creating offline region: $error")
                    continuation.resumeWithException(Exception(error))
                }
            }
        )
    }

    override suspend fun getAllOfflineRegions(): List<OfflineRegion> =
        suspendCancellableCoroutine { continuation ->
            offlineManager.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
                override fun onList(offlineRegions: Array<OfflineRegion>?) {
                    val regions = offlineRegions?.toList() ?: emptyList()
                    Log.d(TAG, "Found ${regions.size} offline regions")
                    continuation.resume(regions)
                }

                override fun onError(error: String) {
                    Log.e(TAG, "Error listing regions: $error")
                    continuation.resumeWithException(Exception(error))
                }
            })
        }

    override suspend fun getDownloadStatus(region: OfflineRegion): OfflineRegionStatus =
        suspendCancellableCoroutine { continuation ->
            region.getStatus(object : OfflineRegion.OfflineRegionStatusCallback {
                override fun onStatus(status: OfflineRegionStatus?) {
                    status?.let { continuation.resume(it) }
                        ?: continuation.resumeWithException(Exception("Null status"))
                }

                override fun onError(error: String?) {
                    continuation.resumeWithException(Exception(error ?: "Unknown error"))
                }
            })
        }

    override suspend fun deleteRegion(region: OfflineRegion): Boolean =
        suspendCancellableCoroutine { continuation ->
            region.delete(object : OfflineRegion.OfflineRegionDeleteCallback {
                override fun onDelete() {
                    Log.d(TAG, "Offline region deleted successfully")
                    continuation.resume(true)
                }

                override fun onError(error: String) {
                    Log.e(TAG, "Error deleting region: $error")
                    continuation.resumeWithException(Exception(error))
                }
            })
        }
}