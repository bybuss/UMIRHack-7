package bob.colbaskin.umirhack7.maplibre.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import bob.colbaskin.umirhack7.maplibre.data.local.entity.FieldEntity
import bob.colbaskin.umirhack7.maplibre.data.local.relations.FieldWithZones
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields")
    fun getFieldsStream(): Flow<List<FieldEntity>>

    @Transaction
    @Query("SELECT * FROM fields")
    fun getFieldsWithZonesStream(): Flow<List<FieldWithZones>>

    @Transaction
    @Query("SELECT * FROM fields WHERE id = :fieldId")
    fun getFieldWithZonesStream(fieldId: Int): Flow<FieldWithZones?>

    @Query("SELECT * FROM fields WHERE id = :fieldId")
    suspend fun getFieldById(fieldId: Int): FieldEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<FieldEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: FieldEntity)

    @Query("DELETE FROM fields")
    suspend fun clearFields()

    @Query("DELETE FROM fields WHERE id = :fieldId")
    suspend fun deleteField(fieldId: Int)
}
