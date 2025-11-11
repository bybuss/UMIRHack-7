package bob.colbaskin.umirhack7.maplibre.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.LoadingScreen
import bob.colbaskin.umirhack7.maplibre.data.models.toLatLngList
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.domain.models.LocationState
import bob.colbaskin.umirhack7.maplibre.utils.LocationPermissionState
import bob.colbaskin.umirhack7.maplibre.utils.rememberLocationPermissionState
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.LOCATION_ZOOM
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.MAP_STYLE_URL
import bob.colbaskin.umirhack7.maplibre.utils.MapLibreConstants.TARGET_ZOOM
import bob.colbaskin.umirhack7.maplibre.utils.getReadableInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.android.offline.OfflineRegion
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre
import org.ramani.compose.Polygon

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreenRoot(
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
                animationDurationMs = 1200

            )
        }
    }

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
                TopBarWithFields(
                    state = state,
                    locationState = locationState,
                    onAction = onAction
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MapLibre(
                    modifier = Modifier.fillMaxSize(),
                    styleBuilder = Style.Builder().fromUri(MAP_STYLE_URL),
                    cameraPosition = cameraPosition.value
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
                            is UiState.Loading -> {
                                Toast.makeText(
                                    LocalContext.current,
                                    "ЗАГРУЗКА",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                state.selectedField?.let { field ->
                    FieldInfoCard(
                        field = field,
                        onClose = { onAction(MapLibreAction.ClearSelectedField) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithFields(
    state: MapLibreState,
    locationState: LocationState,
    onAction: (MapLibreAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = locationState.cityName ?: "Неизвестно",
            modifier = Modifier.weight(1f)
        )

        when (val fieldsState = state.fieldsState) {
            is UiState.Success -> {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(
                            text = state.selectedField?.name ?: "Поля (${fieldsState.data.size})",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = "Список полей"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Все поля") },
                            onClick = {
                                onAction(MapLibreAction.ClearSelectedField)
                                expanded = false
                            }
                        )
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        fieldsState.data.forEach { field ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = field.name,
                                            fontWeight = if (field == state.selectedField) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "Площадь: ${"%.2f".format(field.area / 10000)} га",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                onClick = {
                                    onAction(MapLibreAction.SelectField(field))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
            is UiState.Error -> {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Ошибка загрузки полей",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun FieldInfoCard(field: Field, onClose: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(field.color.toColorInt()))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = field.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Закрыть")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Площадь: ${"%.2f".format(field.area / 10000)} га")
            Text(text = "Количество зон: ${field.zones.size}")

            if (field.zones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Зоны:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                field.zones.forEach { zone ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(zone.color.toColorInt()))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "«${zone.name}» (${"%.2f".format(zone.area / 10000)} га)",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FieldsRenderer(fields: List<Field>, selectedField: Field? = null) {
    fields.forEach { field ->
        val isSelected = field == selectedField

        Polygon(
            vertices = field.geometry.toLatLngList(),
            fillColor = field.color,
            opacity = if (isSelected) 0.6f else 0.4f,
            borderWidth = if (isSelected) 5.0f else 3.0f,
            borderColor = if (isSelected) "#FFD700" else field.color,
        )

        field.zones.forEach { zone ->
            Polygon(
                vertices = zone.geometry.toLatLngList(),
                fillColor = zone.color,
                opacity = if (isSelected) 0.9f else 0.7f,
                borderWidth = if (isSelected) 3.0f else 2.0f,
                borderColor = zone.color,
            )
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
                        onAction(MapLibreAction.CloseFabMenu)
                        onAction(MapLibreAction.ToggleFieldsVisibility)
                    },
                    icon = {
                        Icon(Icons.Default.Layers, contentDescription = "Поля")
                    },
                    text = {
                        Text(if (state.showFields) "Скрыть поля" else "Показать поля")
                    }
                )

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
