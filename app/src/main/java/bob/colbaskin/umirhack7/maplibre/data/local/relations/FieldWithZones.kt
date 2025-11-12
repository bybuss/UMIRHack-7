package bob.colbaskin.umirhack7.maplibre.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import bob.colbaskin.umirhack7.maplibre.data.local.entity.FieldEntity
import bob.colbaskin.umirhack7.maplibre.data.local.entity.ZoneEntity

data class FieldWithZones(
    @Embedded val field: FieldEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "fieldId"
    )
    val zones: List<ZoneEntity>
)
