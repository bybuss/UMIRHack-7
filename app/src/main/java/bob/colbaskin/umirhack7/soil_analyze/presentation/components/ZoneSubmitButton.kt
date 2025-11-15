package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    validationErrors: Map<String, String>,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFormValid = measurementPoint != null &&
            locationError == null &&
            validationErrors.isEmpty()

    Column(modifier = modifier) {
        Button(
            onClick = onAction,
            enabled = !isSubmitting && isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Отправка...")
            } else {
                Text("Отправить анализ почвы")
            }
        }

        if (submitSuccess) {
            Text(
                text = "Анализ успешно отправлен!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (submitError != null) {
            Text(
                text = submitError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (!isFormValid && validationErrors.isNotEmpty()) {
            Text(
                text = "Исправьте ошибки в форме перед отправкой",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
