package bob.colbaskin.umirhack7.maplibre.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "zones",
    foreignKeys = [ForeignKey(
        entity = FieldEntity::class,
        parentColumns = ["id"],
        childColumns = ["fieldId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ZoneEntity(
    @PrimaryKey
    val id: Int,
    val fieldId: Int,
    val name: String,
    val area: Double,
    val color: String,
    val geometryJson: String
)
