package bob.colbaskin.umirhack7.maplibre.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.LoadingScreen
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.common.design_system.utils.getColors
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.presentation.components.FieldInfoCard
import bob.colbaskin.umirhack7.maplibre.presentation.components.TopBarWithSearchAndFields
import bob.colbaskin.umirhack7.maplibre.utils.rememberLocationPermissionState
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.LOCATION_ZOOM
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.MAP_STYLE_URL
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.TARGET_ZOOM
import bob.colbaskin.umirhack7.navigation.Screens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre
import org.ramani.compose.Polygon

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapLibreScreenRoot(
    navController: NavHostController,
    viewModel: MapLibreViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current
    val locationPermissionState = rememberLocationPermissionState()

    LaunchedEffect(Unit) {
        viewModel.onAction(MapLibreAction.LoadFields)
        if (!locationPermissionState.hasPermission) {
            locationPermissionState.requestPermission()
        }
    }

    LaunchedEffect(locationPermissionState.hasPermission) {
        if (locationPermissionState.hasPermission) {
            viewModel.onAction(MapLibreAction.GetCurrentLocation)
        }
    }

    LaunchedEffect(state.regionsState) {
        if (state.regionsState is UiState.Error) {
            Toast.makeText(
                context,
                "${state.regionsState.title}: ${state.regionsState.text}",
                Toast.LENGTH_LONG
            ).show()
            viewModel.onAction(MapLibreAction.ClearError)
        }
    }

    LaunchedEffect(state.locationState.error) {
        state.locationState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.onAction(MapLibreAction.ClearError)
        }
    }

    MapLibreScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is MapLibreAction.NavigateToFieldDetails -> {
                    navController.navigate(
                        Screens.SoilAnalyze(id = action.id)
                    )
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MapLibreScreen(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        contentColor = CustomTheme.colors.black,
        color = CustomTheme.colors.white
    ) {
        if (state.showRegionSuggestion) {
            AlertDialog(
                onDismissRequest = { onAction(MapLibreAction.DismissRegionSuggestion) },
                title = { Text("Скачать оффлайн-карту?", color = CustomTheme.colors.black) },
                text = {
                    Text("Хотите скачать оффлайн-карту для региона ${state.suggestedRegionName}?", color = CustomTheme.colors.black)
                },
                confirmButton = {
                    Button(
                        onClick = { onAction(MapLibreAction.DownloadCurrentRegion) },
                        colors = ButtonDefaults.getColors()
                    ) {
                        Text("Скачать", color = CustomTheme.colors.black)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { onAction(MapLibreAction.DismissRegionSuggestion) },
                        colors = ButtonDefaults.getColors()
                    ) {
                        Text("Не сейчас", color = CustomTheme.colors.black)
                    }
                },
                containerColor = CustomTheme.colors.white,
                textContentColor = CustomTheme.colors.black,
                titleContentColor = CustomTheme.colors.black,
                iconContentColor = CustomTheme.colors.black

            )
        }

        MapContentScreen(
            state = state,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MapContentScreen(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit
) {
    val locationState = state.locationState
    val cameraPosition = remember { mutableStateOf(
        CameraPosition(
            target = locationState.currentLocation?.let {
                LatLng(it.latitude, it.longitude)
            } ?: LatLng(47.1352, 39.4323),
            zoom = LOCATION_ZOOM
        )
    )}

    LaunchedEffect(locationState.currentLocation) {
        locationState.currentLocation?.let { location ->
            cameraPosition.value = CameraPosition(
                target = LatLng(location.latitude, location.longitude),
                zoom = LOCATION_ZOOM
            )
        }
    }

    LaunchedEffect(state.cameraTarget) {
        state.cameraTarget?.let { target ->
            cameraPosition.value = CameraPosition(
                target = target,
                zoom = TARGET_ZOOM,
                animationDurationMs = 1000

            )
        }
    }

    if (state.isLoading) {
        LoadingScreen()
    } else {
        Scaffold (
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBarWithSearchAndFields(
                    state = state,
                    onAction = onAction
                )
            },
            contentColor = CustomTheme.colors.black,
            containerColor = CustomTheme.colors.white
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MapLibre(
                    modifier = Modifier.fillMaxSize(),
                    styleBuilder = Style.Builder().fromUri(MAP_STYLE_URL),
                    cameraPosition = cameraPosition.value,
                    onStyleLoaded = {
                        state.cameraTarget?.let { target ->
                            cameraPosition.value = CameraPosition(
                                target = target,
                                zoom = TARGET_ZOOM
                            )
                        }
                    }
                ) {
                    if (state.showFields) {
                        when (val fieldsState = state.fieldsState) {
                            is UiState.Success -> {
                                FieldsRenderer(
                                    fields = fieldsState.data,
                                    selectedField = state.selectedField
                                )
                            }
                            is UiState.Error -> {
                                Toast.makeText(
                                    LocalContext.current,
                                    fieldsState.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiState.Loading -> {}
                        }
                    }
                }

                state.selectedField?.let { field ->
                    FieldInfoCard(
                        field = field,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldsRenderer(fields: List<Field>, selectedField: Field? = null) {
    fields.forEach { field ->
        val isSelected = field == selectedField

        Polygon(
            vertices = field.geometry.toLatLngList(),
            fillColor = field.color,
            opacity = if (isSelected) 0.6f else 0.4f,
            borderWidth = if (isSelected) 5.0f else 3.0f,
            borderColor = if (isSelected) "#FFD700" else field.color,
        )
    }
}
