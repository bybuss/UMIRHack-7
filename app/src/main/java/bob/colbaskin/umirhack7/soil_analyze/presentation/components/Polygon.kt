package bob.colbaskin.umirhack7.soil_analyze.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import bob.colbaskin.umirhack7.maplibre.domain.models.Geometry

@Composable
fun Polygon(
    modifier: Modifier = Modifier,
    geometry: Geometry,
    fillColor: Color,
    borderColor: Color,
    borderWidth: Float = 3f
) {
    Canvas(modifier = modifier) {
        val vertices = geometry.toNormalizedPoints(size)
        if (vertices.size >= 3) {
            drawPolygon(
                vertices = vertices,
                fillColor = fillColor,
                borderColor = borderColor,
                borderWidth = borderWidth
            )
        }
    }
}

private fun DrawScope.drawPolygon(
    vertices: List<Offset>,
    fillColor: Color,
    borderColor: Color,
    borderWidth: Float
) {
    val path = Path().apply {
        if (vertices.isNotEmpty()) {
            moveTo(vertices[0].x, vertices[0].y)
            vertices.forEachIndexed { index, vertex ->
                if (index > 0) {
                    lineTo(vertex.x, vertex.y)
                }
            }
            close()
        }
    }

    drawPath(path, fillColor, style = Fill)
    drawPath(path, borderColor, style = Stroke(width = borderWidth))
}

private fun Geometry.toNormalizedPoints(size: androidx.compose.ui.geometry.Size): List<Offset> {
    val coordinates = this.coordinates[0]

    var minLon = Double.MAX_VALUE
    var maxLon = Double.MIN_VALUE
    var minLat = Double.MAX_VALUE
    var maxLat = Double.MIN_VALUE

    coordinates.forEach { point ->
        val lon = point[0]
        val lat = point[1]
        minLon = minOf(minLon, lon)
        maxLon = maxOf(maxLon, lon)
        minLat = minOf(minLat, lat)
        maxLat = maxOf(maxLat, lat)
    }

    val lonRange = maxLon - minLon
    val latRange = maxLat - minLat

    return coordinates.map { point ->
        val normalizedX = (point[0] - minLon) / lonRange
        val normalizedY = 1 - (point[1] - minLat) / latRange

        Offset(
            x = (normalizedX * size.width).toFloat(),
            y = (normalizedY * size.height).toFloat()
        )
    }
}
