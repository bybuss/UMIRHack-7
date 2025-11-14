package bob.colbaskin.umirhack7.point_picker.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.MAP_STYLE_URL
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.TARGET_ZOOM
import bob.colbaskin.umirhack7.point_picker.presentation.components.PointPickerBottomBar
import bob.colbaskin.umirhack7.point_picker.presentation.components.PointPickerTopBar
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre
import org.ramani.compose.Polygon

@Composable
fun PointPickerScreen(
    state: PointPickerState,
    onAction: (PointPickerAction) -> Unit,
    onBack: () -> Unit,
    onConfirm: (LatLng) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(state.measurementPoint) {
        if (!state.measurementPoint.isValid && state.measurementPoint.coordinates != null) {
            snackbarHostState.showSnackbar("Точка должна быть внутри зоны!")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PointPickerTopBar(
                onBack = onBack,
                onUseCurrentLocation = { onAction(PointPickerAction.UseCurrentLocation) }
            )
        },
        bottomBar = {
            PointPickerBottomBar(
                isValid = state.measurementPoint.isValid,
                onConfirm = {
                    state.measurementPoint.coordinates?.let(onConfirm)
                },
                onCancel = onBack
            )
        },
        contentColor = CustomTheme.colors.black,
        containerColor = CustomTheme.colors.white,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    Text(
                        text = state.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.zone != null -> {
                    MapContent(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MapContent(
    state: PointPickerState,
    onAction: (PointPickerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraPosition = remember {
        mutableStateOf(
            CameraPosition(
                target = state.cameraTarget ?: LatLng(47.1352, 39.4323),
                zoom = 16.0
            )
        )
    }

    LaunchedEffect(state.cameraTarget) {
        state.cameraTarget?.let { target ->
            cameraPosition.value = CameraPosition(
                target = target,
                zoom = 16.0,
                animationDurationMs = 1000
            )
        }
    }

    LaunchedEffect(state.measurementPoint) {
        if (!state.measurementPoint.isValid && state.measurementPoint.coordinates != null) {
            Toast.makeText(
                context,
                "Точка должна быть внутри зоны!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    MapLibre(
        modifier = modifier,
        styleBuilder = Style.Builder().fromUri(MAP_STYLE_URL),
        cameraPosition = cameraPosition.value,
        onStyleLoaded = {
            state.cameraTarget?.let { target ->
                cameraPosition.value = CameraPosition(
                    target = target,
                    zoom = TARGET_ZOOM
                )
            }
        },
        content = {
            state.zone?.let { zone -> ZoneRenderer(zone = zone) }

            state.measurementPoint.coordinates?.let { point ->
                MeasurementPointRenderer(
                    point = point,
                    isValid = state.measurementPoint.isValid,
                    onPositionChanged = { newPoint ->
                        onAction(PointPickerAction.UpdatePointPosition(newPoint))
                    }
                )
            }
        }
    )
}

@Composable
fun ZoneRenderer(zone: Zone) {
    val vertices = zone.geometry.toLatLngList()

    if (vertices.isNotEmpty()) {
        Polygon(
            vertices = vertices,
            fillColor = zone.color,
            opacity = 0.3f,
            borderWidth = 3.0f,
            borderColor = zone.color
        )
    }
}

@Composable
fun MeasurementPointRenderer(
    point: LatLng,
    isValid: Boolean,
    onPositionChanged: (LatLng) -> Unit
) {
    Circle(
        center = point,
        radius = 12.0f,
        color = if (isValid) "#4CAF50" else "#F44336",
        opacity = 0.9f,
        borderWidth = 2.0f,
        borderColor = if (isValid) "#388E3C" else "#D32F2F",
        zIndex = 1,
        isDraggable = true,
        onCenterDragged = onPositionChanged,
    )
}
