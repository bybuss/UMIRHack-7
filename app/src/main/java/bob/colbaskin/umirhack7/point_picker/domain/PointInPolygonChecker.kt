package bob.colbaskin.umirhack7.point_picker.domain

import org.maplibre.android.geometry.LatLng
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class PointInPolygonChecker @Inject constructor() {

    fun isPointInPolygon(point: LatLng, polygon: List<LatLng>): Boolean {
        if (polygon.size < 3) return false

        var inside = false
        val n = polygon.size

        var j = n - 1
        for (i in 0 until n) {
            val vi = polygon[i]
            val vj = polygon[j]

            if (isPointOnVertex(point, vi)) {
                return true
            }

            if ((vi.latitude > point.latitude) != (vj.latitude > point.latitude) &&
                point.longitude < (vj.longitude - vi.longitude) * (point.latitude - vi.latitude) /
                (vj.latitude - vi.latitude) + vi.longitude) {
                inside = !inside
            }

            j = i
        }

        return inside
    }

    private fun isPointOnVertex(point: LatLng, vertex: LatLng): Boolean {
        val tolerance = 1e-10
        return (abs(point.latitude - vertex.latitude) < tolerance &&
                abs(point.longitude - vertex.longitude) < tolerance)
    }

    private fun isPointOnEdge(point: LatLng, a: LatLng, b: LatLng): Boolean {
        val crossProduct = (point.longitude - a.longitude) * (b.latitude - a.latitude) -
                (point.latitude - a.latitude) * (b.longitude - a.longitude)

        if (abs(crossProduct) > 1e-10) return false

        val dotProduct = (point.longitude - a.longitude) * (b.longitude - a.longitude) +
                (point.latitude - a.latitude) * (b.latitude - a.latitude)

        if (dotProduct < 0) return false

        val squaredLength = (b.longitude - a.longitude) * (b.longitude - a.longitude) +
                (b.latitude - a.latitude) * (b.latitude - a.latitude)

        return dotProduct <= squaredLength
    }
}
