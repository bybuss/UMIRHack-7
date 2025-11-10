package bob.colbaskin.umirhack7.maplibre.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import bob.colbaskin.umirhack7.maplibre.domain.location.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import org.maplibre.android.geometry.LatLng
import java.util.Locale
import javax.inject.Inject

private const val TAG = "LocationRepository"

class LocationRepositoryImpl @Inject constructor(
    private val context: Context
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng? {
        return try {
            Log.d(TAG, "Requesting current location")

            val locationResult = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            locationResult?.let { location ->
                LatLng(location.latitude, location.longitude).also {
                    Log.d(TAG, "Location received: $it")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location: ${e.message}")
            null
        }
    }

    override suspend fun getCityName(latLng: LatLng): String? {
        return try {
            Log.d(TAG, "Geocoding location: $latLng")

            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            addresses?.firstOrNull()?.let { address ->
                val city = address.locality ?: address.subAdminArea ?: address.adminArea
                Log.d(TAG, "City name found: $city")
                city
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error geocoding location: ${e.message}")
            null
        }
    }
}