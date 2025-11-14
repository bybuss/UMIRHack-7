package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.common.design_system.utils.getColors
import bob.colbaskin.umirhack7.soil_analyze.presentation.ZoneLocationAction
import org.maplibre.android.geometry.LatLng

@Composable
fun ZoneLocationSelectionCard(
    measurementPoint: LatLng?,
    locationError: String?,
    onAction: (ZoneLocationAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.getColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Местоположение анализа",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            locationError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomTheme.colors.red,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (locationError == null) {
                LocationOptions(
                    onUseCurrentLocation = { onAction(ZoneLocationAction.UseCurrentLocation) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAction(ZoneLocationAction.OpenMap) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.getColors()
            ) {
                if (measurementPoint == null) {
                    Text(
                        "Выбрать местоположение",
                        color = CustomTheme.colors.black
                    )
                } else {
                    Text(
                        "Изменить местоположение",
                        color = CustomTheme.colors.black
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationOptions(
    onUseCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onUseCurrentLocation,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.getColors()
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
                tint = CustomTheme.colors.black
            )
            Text(
                "Взять мое местоположение",
                color = CustomTheme.colors.black
            )
        }
    }
}
