package bob.colbaskin.umirhack7.maplibre.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bob.colbaskin.umirhack7.maplibre.data.local.entity.ZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {
    @Query("SELECT * FROM zones WHERE fieldId = :fieldId")
    fun getZonesForFieldStream(fieldId: Int): Flow<List<ZoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zones: List<ZoneEntity>)

    @Query("DELETE FROM zones")
    suspend fun clearZones()
}
