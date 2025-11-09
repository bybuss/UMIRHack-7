package bob.colbaskin.umirhack7.maplibre.domain

import org.maplibre.android.geometry.LatLng

interface LocationRepository {
    suspend fun getCurrentLocation(): LatLng?
    suspend fun getCityName(latLng: LatLng): String?
}