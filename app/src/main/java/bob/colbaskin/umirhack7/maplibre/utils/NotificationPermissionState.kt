package bob.colbaskin.umirhack7.maplibre.utils

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@ExperimentalPermissionsApi
class AndroidNotificationPermissionState(
    private val permissionState: PermissionState
) : NotificationPermissionState {
    override val hasPermission: Boolean
        get() = permissionState.status.isGranted

    override fun requestPermission() {
        permissionState.launchPermissionRequest()
    }
}

interface NotificationPermissionState {
    val hasPermission: Boolean
    fun requestPermission()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberNotificationPermissionState(): NotificationPermissionState {
    val permissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )
    return remember(permissionState) {
        AndroidNotificationPermissionState(permissionState)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    val notificationPermissionState = rememberNotificationPermissionState()

    LaunchedEffect(notificationPermissionState.hasPermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            delay(1000)

            if (!notificationPermissionState.hasPermission) {
                notificationPermissionState.requestPermission()
            }
        }
    }
}
