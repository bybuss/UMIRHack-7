package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.soil_analyze.presentation.utils.parseColor

@Composable
fun ZonePolygon(
    zone: Zone,
    modifier: Modifier = Modifier,
    borderWidth: Float = 2f
) {
    Polygon(
        geometry = zone.geometry,
        fillColor = zone.color.parseColor().copy(alpha = 0.6f),
        borderColor = zone.color.parseColor(),
        borderWidth = borderWidth,
        modifier = modifier
    )
}