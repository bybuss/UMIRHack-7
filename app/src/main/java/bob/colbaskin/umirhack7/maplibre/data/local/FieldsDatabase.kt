package bob.colbaskin.umirhack7.maplibre.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import bob.colbaskin.umirhack7.maplibre.data.local.dao.FieldDao
import bob.colbaskin.umirhack7.maplibre.data.local.dao.ZoneDao
import android.content.Context
import bob.colbaskin.umirhack7.maplibre.data.local.entity.FieldEntity
import bob.colbaskin.umirhack7.maplibre.data.local.entity.ZoneEntity

@Database(
    entities = [FieldEntity::class, ZoneEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FieldsDatabase : RoomDatabase() {
    abstract fun fieldDao(): FieldDao
    abstract fun zoneDao(): ZoneDao

    companion object {
        @Volatile
        private var INSTANCE: FieldsDatabase? = null

        fun getInstance(context: Context): FieldsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FieldsDatabase::class.java,
                    "fields_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}