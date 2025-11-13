package bob.colbaskin.umirhack7.point_picker.data

import bob.colbaskin.umirhack7.maplibre.data.local.dao.ZoneDao
import bob.colbaskin.umirhack7.maplibre.data.models.GeometryDTO
import bob.colbaskin.umirhack7.maplibre.domain.models.Geometry
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import bob.colbaskin.umirhack7.point_picker.domain.ZoneRepository
import jakarta.inject.Inject
import kotlinx.serialization.json.Json

class ZoneRepositoryImpl @Inject constructor(
    private val zoneDao: ZoneDao
) : ZoneRepository {

    override suspend fun getZoneById(zoneId: Int): Zone? {
        return try {
            val zoneEntity = zoneDao.getZoneById(zoneId)
            zoneEntity?.let { entity ->
                Zone(
                    id = entity.id,
                    name = entity.name,
                    area = entity.area,
                    color = entity.color,
                    geometry = parseGeometry(entity.geometryJson)
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseGeometry(geometryJson: String): Geometry {
        val geometryDto = Json.decodeFromString<GeometryDTO>(geometryJson)
        return Geometry(
            type = geometryDto.type,
            coordinates = geometryDto.coordinates
        )
    }
}