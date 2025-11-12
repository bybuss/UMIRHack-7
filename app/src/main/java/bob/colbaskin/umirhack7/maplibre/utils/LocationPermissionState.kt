package bob.colbaskin.umirhack7.maplibre.utils

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
class AndroidLocationPermissionState(
    private val permissionState: PermissionState
) : LocationPermissionState {
    override val hasPermission: Boolean
        get() = permissionState.status.isGranted

    override fun requestPermission() {
        permissionState.launchPermissionRequest()
    }
}

interface LocationPermissionState {
    val hasPermission: Boolean
    fun requestPermission()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionState(): LocationPermissionState {
    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    return remember(permissionState) {
        AndroidLocationPermissionState(permissionState)
    }
}