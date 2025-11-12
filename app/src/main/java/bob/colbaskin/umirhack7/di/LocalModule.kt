package bob.colbaskin.umirhack7.di

import android.content.Context
import bob.colbaskin.umirhack7.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.umirhack7.common.user_prefs.data.datastore.UserPreferencesSerializer
import bob.colbaskin.umirhack7.maplibre.data.local.FieldsDatabase
import bob.colbaskin.umirhack7.maplibre.data.local.dao.FieldDao
import bob.colbaskin.umirhack7.maplibre.data.local.dao.ZoneDao
import bob.colbaskin.umirhack7.maplibre.data.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideUserPreferencesSerializer(): UserPreferencesSerializer {
        return UserPreferencesSerializer
    }

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): UserDataStore {
        return UserDataStore(context = context)
    }

    @Provides
    @Singleton
    fun provideFieldsDatabase(@ApplicationContext context: Context): FieldsDatabase {
        return FieldsDatabase.getInstance(context)
    }

    @Provides
    fun provideFieldDao(database: FieldsDatabase): FieldDao {
        return database.fieldDao()
    }

    @Provides
    fun provideZoneDao(database: FieldsDatabase): ZoneDao {
        return database.zoneDao()
    }

    @Provides
    @Singleton
    fun provideSyncManager(@ApplicationContext context: Context): SyncManager {
        return SyncManager(context)
    }
}
