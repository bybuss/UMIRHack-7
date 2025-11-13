package bob.colbaskin.umirhack7.soil_analyze.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.LoadingScreen
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.point_picker.presentation.PointPickerActivity
import bob.colbaskin.umirhack7.soil_analyze.presentation.components.ZonePolygon
import org.maplibre.android.geometry.LatLng
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import bob.colbaskin.umirhack7.soil_analyze.presentation.components.SoilAnalyzeTopBar
import bob.colbaskin.umirhack7.soil_analyze.presentation.components.ZoneSoilAnalysisForm

@Composable
fun SoilAnalyzeScreenRoot(
    navController: NavHostController,
    viewModel: SoilAnalyzeViewModel = hiltViewModel()
) {
    val fieldId: Int = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 1
    val context = LocalContext.current
    val state = viewModel.state

    val pointPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val location = PointPickerActivity.getResultPoint(data)
                val point = LatLng(location.latitude, location.longitude)

                state.expandedZoneId?.let { zoneId ->
                    viewModel.onAction(SoilAnalyzeAction.UpdateZoneMeasurementPoint(zoneId, point))
                }
                Toast.makeText(
                    context,
                    "Точка успешно выбрана",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(SoilAnalyzeAction.LoadFieldDetail(fieldId))
    }

    SoilAnalyzeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SoilAnalyzeAction.OpenMapForZone -> {
                    val intent = PointPickerActivity.createIntent(context, action.zoneId)
                    pointPickerLauncher.launch(intent)
                }
                else -> viewModel.onAction(action)
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@Composable
private fun SoilAnalyzeScreen(
    state: SoilAnalyzeState,
    onAction: (SoilAnalyzeAction) -> Unit,
    onBack: () -> Unit
) {
    when (val stateField = state.fieldDetailState) {
        is UiState.Error -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = "Ошибка загрузки данных поля")
            }
        }
        UiState.Loading -> LoadingScreen()
        is UiState.Success -> {
            stateField.data?.let { field ->
                Scaffold(
                    topBar = {
                        SoilAnalyzeTopBar(
                            field = field,
                            onBack = onBack
                        )
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Участки поля (${field.zones.size})",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(field.zones) { zone ->
                            val zoneState = state.getZoneAnalysisState(zone.id)
                            ExpandableZoneCard(
                                zone = zone,
                                isExpanded = state.expandedZoneId == zone.id,
                                soilAnalysisData = zoneState.soilAnalysisData,
                                measurementPoint = zoneState.measurementPoint,
                                locationError = zoneState.locationError,
                                isSubmitting = zoneState.isSubmitting,
                                submitError = zoneState.submitError,
                                submitSuccess = zoneState.submitSuccess,
                                onToggleExpansion = {
                                    onAction(SoilAnalyzeAction.ToggleZoneExpansion(zone.id))
                                },
                                onAction = onAction,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            } ?: run {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Данные поля не найдены!")
                }
            }
        }
    }
}

@Composable
fun ExpandableZoneCard(
    zone: Zone,
    isExpanded: Boolean,
    soilAnalysisData: SoilAnalysisData,
    measurementPoint: LatLng?,
    locationError: String?,
    isSubmitting: Boolean,
    submitError: String?,
    submitSuccess: Boolean,
    onToggleExpansion: () -> Unit,
    onAction: (SoilAnalyzeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ZonePolygon(
                    zone = zone,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = zone.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Площадь: ${"%.2f".format(zone.area / 10000)} га",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleExpansion) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Свернуть" else "Раскрыть"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Анализ почвы для зоны ${zone.name}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ZoneSoilAnalysisForm(
                        zoneId = zone.id,
                        soilAnalysisData = soilAnalysisData,
                        measurementPoint = measurementPoint,
                        locationError = locationError,
                        isSubmitting = isSubmitting,
                        submitError = submitError,
                        submitSuccess = submitSuccess,
                        onAction = onAction,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
