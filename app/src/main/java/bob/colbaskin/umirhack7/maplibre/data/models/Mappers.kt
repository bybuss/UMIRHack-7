package bob.colbaskin.umirhack7.maplibre.data.models

import bob.colbaskin.umirhack7.maplibre.data.local.entity.FieldEntity
import bob.colbaskin.umirhack7.maplibre.data.local.entity.ZoneEntity
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.domain.models.Geometry
import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import kotlinx.serialization.json.Json
import org.maplibre.android.geometry.LatLng

fun FieldDTO.toDomain(): Field {
    return Field(
        id = this.id,
        name = this.name,
        area = this.area,
        color = this.color,
        geometry = this.geometry.toDomain(),
        zones = this.zones.map { it.toDomain() }
    )
}

fun GeometryDTO.toDomain(): Geometry {
    return Geometry(
        type = this.type,
        coordinates = this.coordinates
    )
}

fun ZoneDTO.toDomain(): Zone {
    return Zone(
        id = this.id,
        name = this.name,
        area = this.area,
        color = this.color,
        geometry = this.geometry.toDomain()
    )
}

fun Geometry.toLatLngList(): List<LatLng> {
    return this.coordinates.first().map { coordinate ->
        LatLng(coordinate[1], coordinate[0])
    }
}

fun FieldDTO.toEntity(): FieldEntity {
    return FieldEntity(
        id = this.id,
        name = this.name,
        area = this.area,
        color = this.color,
        geometryJson = Json.encodeToString(this.geometry)
    )
}

fun ZoneDTO.toEntity(fieldId: Int): ZoneEntity {
    return ZoneEntity(
        id = this.id,
        fieldId = fieldId,
        name = this.name,
        area = this.area,
        color = this.color,
        geometryJson = Json.encodeToString(this.geometry)
    )
}

fun FieldEntity.toDomain(zones: List<Zone>): Field {
    val geometryDto = Json.decodeFromString<GeometryDTO>(this.geometryJson)
    return Field(
        id = this.id,
        name = this.name,
        area = this.area,
        color = this.color,
        geometry = geometryDto.toDomain(),
        zones = zones
    )
}

fun ZoneEntity.toDomain(): Zone {
    val geometryDto = Json.decodeFromString<GeometryDTO>(this.geometryJson)
    return Zone(
        id = this.id,
        name = this.name,
        area = this.area,
        color = this.color,
        geometry = geometryDto.toDomain()
    )
}
