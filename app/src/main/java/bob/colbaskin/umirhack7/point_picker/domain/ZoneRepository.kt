package bob.colbaskin.umirhack7.point_picker.domain

import bob.colbaskin.umirhack7.maplibre.domain.models.Zone

interface ZoneRepository {
    suspend fun getZoneById(zoneId: Int): Zone?
}
