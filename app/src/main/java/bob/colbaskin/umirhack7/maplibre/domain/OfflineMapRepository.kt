package bob.colbaskin.umirhack7.maplibre.domain

import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineRegionStatus

interface OfflineMapRepository {
    suspend fun downloadRegion(
        styleUrl: String,
        bounds: LatLngBounds,
        minZoom: Double,
        maxZoom: Double,
        regionName: String
    ): OfflineRegion

    suspend fun getAllOfflineRegions(): List<OfflineRegion>
    suspend fun getDownloadStatus(region: OfflineRegion): OfflineRegionStatus
    suspend fun deleteRegion(region: OfflineRegion): Boolean
}