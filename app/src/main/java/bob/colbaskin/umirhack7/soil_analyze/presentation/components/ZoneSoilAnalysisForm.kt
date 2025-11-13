package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import bob.colbaskin.umirhack7.soil_analyze.presentation.SoilAnalyzeAction
import bob.colbaskin.umirhack7.soil_analyze.presentation.ZoneLocationAction
import org.maplibre.android.geometry.LatLng

@Composable
fun ZoneSoilAnalysisForm(
    zoneId: Int,
    soilAnalysisData: SoilAnalysisData,
    measurementPoint: LatLng?,
    locationError: String?,
    isSubmitting: Boolean,
    submitError: String?,
    submitSuccess: Boolean,
    onAction: (SoilAnalyzeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        measurementPoint?.let { point ->
            Text(
                text = "Координаты: ${"%.6f".format(point.latitude)}, ${"%.6f".format(point.longitude)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        ZoneLocationSelectionCard(
            measurementPoint = measurementPoint,
            locationError = locationError,
            onAction = { action ->
                when (action) {
                    is ZoneLocationAction.UseCurrentLocation -> onAction(SoilAnalyzeAction.UseCurrentLocationForZone(zoneId))
                    is ZoneLocationAction.ShowOptions -> onAction(SoilAnalyzeAction.ShowZoneLocationOptions(zoneId))
                    is ZoneLocationAction.HideOptions -> onAction(SoilAnalyzeAction.HideZoneLocationOptions(zoneId))
                    is ZoneLocationAction.OpenMap -> onAction(SoilAnalyzeAction.OpenMapForZone(zoneId))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // TODO: Добавить валидацию полей
        AnalysisFieldWithHint(
            value = soilAnalysisData.N.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(N = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Содержание азота (N)",
            hint = "Количество азота в почве в мг/кг",
            modifier = Modifier.fillMaxWidth()
        )

        AnalysisFieldWithHint(
            value = soilAnalysisData.P.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(P = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Содержание фосфора (P)",
            hint = "Количество фосфора в почве в мг/кг",
            modifier = Modifier.fillMaxWidth()
        )

        AnalysisFieldWithHint(
            value = soilAnalysisData.K.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(K = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Содержание калия (K)",
            hint = "Количество калия в почве в мг/кг",
            modifier = Modifier.fillMaxWidth()
        )

        AnalysisFieldWithHint(
            value = soilAnalysisData.Temperature.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(Temperature = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Температура почвы",
            hint = "Температура почвы на глубине измерения в °C",
            modifier = Modifier.fillMaxWidth()
        )

        AnalysisFieldWithHint(
            value = soilAnalysisData.Humidity.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(Humidity = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Влажность почвы",
            hint = "Процентное содержание влаги в почве",
            modifier = Modifier.fillMaxWidth()
        )

        AnalysisFieldWithHint(
            value = soilAnalysisData.pH.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(pH = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Уровень pH",
            hint = "Кислотность почвы по шкале pH (0-14)",
            modifier = Modifier.fillMaxWidth()
        )

        AnalysisFieldWithHint(
            value = soilAnalysisData.RainFall.toString(),
            onValueChange = { value ->
                val newData = soilAnalysisData.copy(RainFall = value.toDoubleOrNull() ?: 0.0)
                onAction(SoilAnalyzeAction.UpdateZoneSoilAnalysisData(zoneId, newData))
            },
            label = "Количество осадков",
            hint = "Высота слоя воды (в миллиметрах), которая выпала бы на ровную поверхность, если бы она не испарялась, не просачивалась в почву и не стекала",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ZoneSubmitButton(
            isSubmitting = isSubmitting,
            submitError = submitError,
            submitSuccess = submitSuccess,
            measurementPoint = measurementPoint,
            locationError = locationError,
            onAction = { onAction(SoilAnalyzeAction.SubmitZoneAnalysis(zoneId)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
