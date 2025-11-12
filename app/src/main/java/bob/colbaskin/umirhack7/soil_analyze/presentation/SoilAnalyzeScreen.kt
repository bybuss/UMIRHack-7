package bob.colbaskin.umirhack7.soil_analyze.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.LoadingScreen
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.soil_analyze.presentation.components.FieldPolygon
import bob.colbaskin.umirhack7.soil_analyze.presentation.components.ZonePolygon

@Composable
fun SoilAnalyzeScreenRoot(
    navController: NavHostController,
    viewModel: SoilAnalyzeViewModel = hiltViewModel()
) {
    val fieldId: Int
            = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 1

    LaunchedEffect(Unit) {
        viewModel.onAction(SoilAnalyzeAction.LoadFieldDetail(fieldId))
    }

    SoilAnalyzeScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun SoilAnalyzeScreen(
    state: SoilAnalyzeState,
    onAction: (SoilAnalyzeAction) -> Unit
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = field.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Text(
                                    text = "Площадь: ${"%.2f".format(field.area / 10000)} га",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(top = 16.dp)
                                ) {
                                    FieldPolygon(
                                        field = field,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Зоны (${field.zones.size})",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(field.zones) { zone ->
                        ZoneCard(
                            zone = zone,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            } ?: run {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Данные поля не найдены")
                }
            }
        }
    }
}

@Composable
private fun ZoneCard(
    zone: Zone,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            ZonePolygon(
                zone = zone,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )
        }
    }
}
