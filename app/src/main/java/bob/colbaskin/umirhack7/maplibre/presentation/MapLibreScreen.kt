package bob.colbaskin.umirhack7.maplibre.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.umirhack7.common.design_system.theme.UMIRHack7Theme
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.LocationState
import bob.colbaskin.umirhack7.maplibre.presentation.location.LocationPermissionState
import bob.colbaskin.umirhack7.maplibre.presentation.location.rememberLocationPermissionState
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
        locationPermissionState = locationPermissionState
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit,
    locationPermissionState: LocationPermissionState
) {
    UMIRHack7Theme {
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
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainMapScreen(
    regions: List<OfflineRegion>,
    isLoading: Boolean,
    locationState: LocationState,
    locationPermissionState: LocationPermissionState,
    onAction: (MapLibreAction) -> Unit
) {
    Column {
        if (locationState.currentLocation != null) {
            Text(
                text = "Ваше местоположение: ${locationState.cityName ?: "Неизвестно"}",
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(
            onClick = { onAction(MapLibreAction.GetCurrentLocation) },
            modifier = Modifier.padding(16.dp),
            enabled = !isLoading && locationPermissionState.hasPermission
        ) {
            if (locationState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Обновить местоположение")
            }
        }

        Button(
            onClick = { onAction(MapLibreAction.LoadOfflineRegions) },
            modifier = Modifier.padding(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Обновить список регионов")
            }
        }

        if (!locationPermissionState.hasPermission) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Для определения вашего региона необходимо разрешение на геолокацию",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        locationPermissionState.requestPermission()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Запросить разрешение на геолокацию")
                }
            }
        }

        Text(
            text = "Оффлайн регионов: ${regions.size}",
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Список регионов: ${regions.map { it.id }}",
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Отладка: разрешение = ${locationPermissionState.hasPermission}",
            modifier = Modifier.padding(16.dp)
        )

        MaplibreMap(
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
            options = MapOptions(
                renderOptions = RenderOptions.Standard,
                gestureOptions = GestureOptions.Standard,
                ornamentOptions = OrnamentOptions.AllDisabled
            ),
            modifier = Modifier.weight(1f)
        )
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
