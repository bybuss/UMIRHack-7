package bob.colbaskin.umirhack7.soil_analyze.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import bob.colbaskin.umirhack7.soil_analyze.data.local.entity.SoilAnalysisQueueEntity
import bob.colbaskin.umirhack7.soil_analyze.data.models.AnalysisQueueStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SoilAnalysisQueueDao {

    @Insert
    suspend fun insert(analysis: SoilAnalysisQueueEntity)

    @Update
    suspend fun update(analysis: SoilAnalysisQueueEntity)

    @Query("SELECT * FROM soil_analysis_queue WHERE status = :status ORDER BY createdAt ASC")
    suspend fun getByStatus(status: AnalysisQueueStatus): List<SoilAnalysisQueueEntity>

    @Query("SELECT COUNT(*) FROM soil_analysis_queue WHERE status = 'PENDING' OR status = 'FAILED'")
    fun getPendingCount(): Flow<Int>

    @Query("DELETE FROM soil_analysis_queue WHERE status = 'SUCCESS' AND createdAt < :olderThan")
    suspend fun deleteOldSuccess(olderThan: Date)

    @Query("SELECT * FROM soil_analysis_queue WHERE id = :id")
    suspend fun getById(id: String): SoilAnalysisQueueEntity?
}
