package bob.colbaskin.umirhack7.maplibre.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState

@Composable
fun MainScreenRoot(
    viewModel: MapLibreViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(state.collectAsState().value.error) {
        state.value.let { error ->
            Toast.makeText(
                context,
                "Ошибка: $error",
                Toast.LENGTH_LONG
            ).show()
            viewModel.onAction(MapLibreAction.ClearError)
        }
    }

    MainScreen(
        state = state.collectAsState().value,
        onAction = viewModel::onAction
    )
}

@Composable
fun MainScreen(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit,
) {
    UMIRHack7Theme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (state.showDownloadScreen) {
                DownloadScreen(
                    progress = state.downloadProgress,
                    onCancel = { onAction(MapLibreAction.CancelDownload) }
                )
            } else {
                MainMapScreen(
                    state = state,
                    onAction = onAction
                )
            }
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

@Composable
fun MainMapScreen(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit
) {
    Column {
        Button(
            onClick = { onAction(MapLibreAction.DownloadMoscowMap) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Скачать оффлайн-карту Москвы")
        }

        Button(
            onClick = { onAction(MapLibreAction.LoadOfflineRegions) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Обновить список регионов")
        }

        Text(
            text = "Оффлайн регионов: ${state.offlineRegions.size}",
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Список регионов: ${state.offlineRegions.map { it.id }}",
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