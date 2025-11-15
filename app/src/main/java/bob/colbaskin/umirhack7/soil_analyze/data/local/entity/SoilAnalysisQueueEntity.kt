package bob.colbaskin.umirhack7.soil_analyze.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import bob.colbaskin.umirhack7.soil_analyze.data.local.converters.DateConverter
import bob.colbaskin.umirhack7.soil_analyze.data.models.AnalysisQueueStatus
import java.util.Date
import java.util.UUID

@Entity(tableName = "soil_analysis_queue")
@TypeConverters(DateConverter::class)
data class SoilAnalysisQueueEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val N: Double,
    val P: Double,
    val K: Double,
    val Temperature: Double,
    val Humidity: Double,
    val pH: Double,
    val RainFall: Double,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Date,
    val status: AnalysisQueueStatus = AnalysisQueueStatus.PENDING,
    val attemptCount: Int = 0,
    val lastAttemptDate: Date? = null,
    val errorMessage: String? = null
)
