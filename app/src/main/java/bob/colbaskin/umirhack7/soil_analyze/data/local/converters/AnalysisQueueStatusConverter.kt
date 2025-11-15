package bob.colbaskin.umirhack7.soil_analyze.data.local.converters

import androidx.room.TypeConverter
import bob.colbaskin.umirhack7.soil_analyze.data.models.AnalysisQueueStatus

class AnalysisQueueStatusConverter {
    @TypeConverter
    fun fromStatus(status: AnalysisQueueStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(status: String): AnalysisQueueStatus {
        return AnalysisQueueStatus.valueOf(status)
    }
}
