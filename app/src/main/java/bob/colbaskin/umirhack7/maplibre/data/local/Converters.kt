package bob.colbaskin.umirhack7.maplibre.data.local

import androidx.room.TypeConverter
import bob.colbaskin.umirhack7.maplibre.data.models.GeometryDTO
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromGeometryToString(geometry: GeometryDTO): String {
        return Json.encodeToString(geometry)
    }

    @TypeConverter
    fun fromStringToGeometry(geometryString: String): GeometryDTO {
        return Json.decodeFromString(geometryString)
    }
}