package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.maplibre.android.geometry.LatLng

@Composable
fun ZoneSubmitButton(
    isSubmitting: Boolean,
    submitError: String?,
    submitSuccess: Boolean,
    measurementPoint: LatLng?,
    locationError: String?,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isButtonEnabled = measurementPoint != null && locationError == null && !isSubmitting && !submitSuccess

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSubmitting) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text("Отправка данных...")
                }
            } else if (submitSuccess) {
                Text(
                    text = "Данные успешно отправлены!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isButtonEnabled
                ) {
                    Text("Отправить анализ почвы")
                }

                if (!isButtonEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            locationError != null -> locationError
                            measurementPoint == null -> "Для отправки необходимо указать местоположение"
                            else -> "Невозможно отправить анализ"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                submitError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
