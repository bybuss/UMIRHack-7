package bob.colbaskin.umirhack7.maplibre.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val area: Double,
    val color: String,
    val geometryJson: String
)
