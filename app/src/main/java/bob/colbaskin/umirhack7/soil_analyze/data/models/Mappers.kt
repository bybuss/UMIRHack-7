package bob.colbaskin.umirhack7.soil_analyze.data.models

import bob.colbaskin.umirhack7.soil_analyze.domain.models.AnalysisLocation
import org.maplibre.android.geometry.LatLng

fun LatLng.toAnalysisLocation(): AnalysisLocation {
    return AnalysisLocation(
        coordinates = listOf(
            this.longitude.toString(),
            this.latitude.toString()
        )
    )
}
