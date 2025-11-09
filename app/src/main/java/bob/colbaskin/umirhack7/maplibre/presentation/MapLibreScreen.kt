package bob.colbaskin.umirhack7.maplibre.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.LoadingScreen
import bob.colbaskin.umirhack7.maplibre.domain.models.LocationState
import bob.colbaskin.umirhack7.maplibre.presentation.location.LocationPermissionState
import bob.colbaskin.umirhack7.maplibre.presentation.location.rememberLocationPermissionState
import bob.colbaskin.umirhack7.maplibre.utils.getReadableInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.maplibre.android.offline.OfflineRegion

@Composable
fun MainScreenRoot(
    viewModel: MapLibreViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current
    val locationPermissionState = rememberLocationPermissionState()

    LaunchedEffect(locationPermissionState.hasPermission) {
        if (locationPermissionState.hasPermission) {
            viewModel.onAction(MapLibreAction.RequestLocationPermission)
            viewModel.onAction(MapLibreAction.GetCurrentLocation)
        }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionState.hasPermission) {
            locationPermissionState.requestPermission()
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

    LaunchedEffect(state.downloadState) {
        if (state.downloadState is UiState.Error) {
            Toast.makeText(
                context,
                "${state.downloadState.title}: ${state.downloadState.text}",
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

    MainScreen(
        state = state,
        onAction = viewModel::onAction,
        locationPermissionState = locationPermissionState,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit,
    locationPermissionState: LocationPermissionState
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        if (state.showRegionSuggestion) {
            AlertDialog(
                onDismissRequest = { onAction(MapLibreAction.DismissRegionSuggestion) },
                title = { Text("Скачать оффлайн-карту?") },
                text = {
                    Text("Хотите скачать оффлайн-карту для региона ${state.suggestedRegionName}?")
                },
                confirmButton = {
                    Button(
                        onClick = { onAction(MapLibreAction.DownloadCurrentRegion) }
                    ) {
                        Text("Скачать")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { onAction(MapLibreAction.DismissRegionSuggestion) }
                    ) {
                        Text("Не сейчас")
                    }
                }
            )
        }

        when {
            state.showDownloadScreen -> {
                DownloadScreen(
                    progress = state.downloadProgress,
                    onCancel = { onAction(MapLibreAction.CancelDownload) }
                )
            }

            else -> {
                when (val regionsState = state.regionsState) {
                    is UiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Загрузка регионов...")
                        }
                    }

                    is UiState.Success -> {
                        MainMapScreen(
                            state = state,
                            regions = regionsState.data,
                            isLoading = state.isLoading,
                            locationState = state.locationState,
                            locationPermissionState = locationPermissionState,
                            onAction = onAction
                        )
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Ошибка загрузки регионов")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { onAction(MapLibreAction.LoadOfflineRegions) }) {
                                Text("Повторить")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainMapScreen(
    state: MapLibreState,
    regions: List<OfflineRegion>,
    isLoading: Boolean,
    locationState: LocationState,
    locationPermissionState: LocationPermissionState,
    onAction: (MapLibreAction) -> Unit
) {
    if (isLoading) {
        LoadingScreen()
    } else {
        Scaffold (
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FAB(
                    state = state,
                    onAction = onAction,
                    locationPermissionState = locationPermissionState,
                    regions = regions
                )
            },
            topBar = {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = locationState.cityName ?: "Неизвестно")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MaplibreMap(
                    baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
                    options = MapOptions(
                        renderOptions = RenderOptions.Standard,
                        gestureOptions = GestureOptions.Standard,
                        ornamentOptions = OrnamentOptions.AllDisabled
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun FAB(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit,
    locationPermissionState: LocationPermissionState,
    regions: List<OfflineRegion>,
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = state.isFabExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                ExtendedFloatingActionButton(
                    onClick = {
                        Log.i("REGIONS", "регионы: ${regions.map { it.getReadableInfo() }}")
                    },
                    icon = {
                        Icon(Icons.Default.Info, contentDescription = "Информация")
                    },
                    text = {
                        Text("регионы: ${regions.map { it.getReadableInfo() }}")
                    }
                )

                ExtendedFloatingActionButton(
                    onClick = {
                        onAction(MapLibreAction.CloseFabMenu)
                        onAction(MapLibreAction.GetCurrentLocation)
                    },
                    icon = {
                        if (state.locationState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Местоположение"
                            )
                        }
                    },
                    text = {
                        Text("Обновить местоположение")
                    }
                )

                ExtendedFloatingActionButton(
                    onClick = {
                        if (!state.isLoading) {
                            onAction(MapLibreAction.CloseFabMenu)
                            onAction(MapLibreAction.LoadOfflineRegions)
                        } else {
                            Toast.makeText(context, "Подождите окончание загрузки!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Обновить регионы")
                        }
                    },
                    text = {
                        Text("Скачать оффлайн карту региона")
                    },
                )

                if (!locationPermissionState.hasPermission) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            onAction(MapLibreAction.CloseFabMenu)
                            locationPermissionState.requestPermission()
                        },
                        icon = {
                            Icon(Icons.Default.NearbyError, contentDescription = "Разрешение")
                        },
                        text = {
                            Text("Запросить разрешение")
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onAction(MapLibreAction.ToggleFabExpand) }
        ) {
            Icon(
                imageVector = if (state.isFabExpanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Меню"
            )
        }
    }
}

@Composable
fun DownloadScreen(
    progress: Float,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Загрузка оффлайн-карты...")
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        Text("${(progress * 100).toInt()}%")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCancel) {
            Text("Отменить загрузку")
        }
    }
}
