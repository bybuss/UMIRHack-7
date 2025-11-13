package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.soil_analyze.utils.parseColor

@Composable
fun FieldPolygon(
    field: Field,
    modifier: Modifier = Modifier,
    borderWidth: Float = 3f
) {
    Polygon(
        geometry = field.geometry,
        fillColor = field.color.parseColor().copy(alpha = 0.4f),
        borderColor = field.color.parseColor(),
        borderWidth = borderWidth,
        modifier = modifier
    )
}
