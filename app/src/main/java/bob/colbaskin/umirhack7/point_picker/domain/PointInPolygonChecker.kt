package bob.colbaskin.umirhack7.point_picker.domain

import org.maplibre.android.geometry.LatLng
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointInPolygonChecker @Inject constructor() {

    fun isPointInPolygon(point: LatLng, polygon: List<LatLng>): Boolean {
        if (polygon.size < 3) return false

        var crossings = 0
        val vertices = polygon + polygon.first()

        for (i in 0 until polygon.size) {
            val a = vertices[i]
            val b = vertices[i + 1]

            if (rayCrossesSegment(point, a, b)) {
                crossings++
            }
        }

        return crossings % 2 == 1
    }

    private fun rayCrossesSegment(point: LatLng, a: LatLng, b: LatLng): Boolean {
        val px = point.longitude
        val py = point.latitude
        val ax = a.longitude
        val ay = a.latitude
        val bx = b.longitude
        val by = b.latitude

        if ((ay > py && by > py) || (ay < py && by < py)) {
            return false
        }

        if (px >= maxOf(ax, bx)) {
            return false
        }

        if (px < minOf(ax, bx)) {
            return true
        }

        val red = if (ax != bx) (by - ay) / (bx - ax) else Double.MAX_VALUE
        val blue = if (ax != px) (py - ay) / (px - ax) else Double.MAX_VALUE

        return blue > red
    }
}
