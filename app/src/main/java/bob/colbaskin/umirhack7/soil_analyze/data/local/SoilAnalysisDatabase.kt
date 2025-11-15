package bob.colbaskin.umirhack7.soil_analyze.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import bob.colbaskin.umirhack7.soil_analyze.data.local.converters.AnalysisQueueStatusConverter
import bob.colbaskin.umirhack7.soil_analyze.data.local.converters.DateConverter
import bob.colbaskin.umirhack7.soil_analyze.data.local.dao.SoilAnalysisQueueDao
import bob.colbaskin.umirhack7.soil_analyze.data.local.entity.SoilAnalysisQueueEntity

@Database(
    entities = [SoilAnalysisQueueEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, AnalysisQueueStatusConverter::class)
abstract class SoilAnalysisDatabase : RoomDatabase() {
    abstract fun soilAnalysisQueueDao(): SoilAnalysisQueueDao

    companion object {
        @Volatile
        private var INSTANCE: SoilAnalysisDatabase? = null

        fun getInstance(context: Context): SoilAnalysisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoilAnalysisDatabase::class.java,
                    "soil_analysis_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
