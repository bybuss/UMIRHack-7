package bob.colbaskin.umirhack7.maplibre.domain.models

import org.maplibre.android.geometry.LatLng

data class LocationState(
    val hasPermission: Boolean = false,
    val currentLocation: LatLng? = null,
    val cityName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
