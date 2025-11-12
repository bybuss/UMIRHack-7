package bob.colbaskin.umirhack7.di

import android.app.NotificationManager
import android.content.Context
import bob.colbaskin.umirhack7.auth.data.AuthRepositoryImpl
import bob.colbaskin.umirhack7.auth.data.RefreshTokenRepositoryImpl
import bob.colbaskin.umirhack7.auth.domain.auth.AuthApiService
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.auth.domain.token.RefreshTokenRepository
import bob.colbaskin.umirhack7.auth.domain.token.RefreshTokenService
import bob.colbaskin.umirhack7.common.user_prefs.data.UserPreferencesRepositoryImpl
import bob.colbaskin.umirhack7.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.umirhack7.di.token.TokenManager
import bob.colbaskin.umirhack7.maplibre.data.location.LocationRepositoryImpl
import bob.colbaskin.umirhack7.maplibre.data.OfflineMapRepositoryImpl
import bob.colbaskin.umirhack7.maplibre.data.notifocation.NotificationRepositoryImpl
import bob.colbaskin.umirhack7.maplibre.domain.location.LocationRepository
import bob.colbaskin.umirhack7.maplibre.domain.NotificationRepository
import bob.colbaskin.umirhack7.maplibre.domain.OfflineMapRepository
import bob.colbaskin.umirhack7.maplibre.data.fields.FieldsRepositoryImpl
import bob.colbaskin.umirhack7.maplibre.data.local.FieldsDatabase
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsRepository
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsService
import bob.colbaskin.umirhack7.profile.data.ProfileRepositoryImpl
import bob.colbaskin.umirhack7.profile.domain.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.maplibre.android.offline.OfflineManager
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: UserDataStore): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApiService,
        userPreferences: UserPreferencesRepository,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            authApi = authApi,
            userPreferences = userPreferences,
            tokenManager = tokenManager
        )
    }

    @Provides
    @Singleton
    fun provideRefreshTokenService(retrofit: Retrofit): RefreshTokenService {
        return retrofit.create(RefreshTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenRepository(
        refreshTokenApi: RefreshTokenService,
        tokenManager: TokenManager
    ): RefreshTokenRepository {
        return RefreshTokenRepositoryImpl(
            refreshTokenApi = refreshTokenApi,
            tokenManager = tokenManager
        )
    }

    @Provides
    @Singleton
    fun provideOfflineManager(@ApplicationContext context: Context): OfflineManager {
        return OfflineManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideOfflineMapRepository(offlineManager: OfflineManager): OfflineMapRepository {
        return OfflineMapRepositoryImpl(offlineManager = offlineManager)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepository {
        return LocationRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        tokenManager: TokenManager,
        userPreferences: UserPreferencesRepository
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            tokenManager = tokenManager,
            userPreferences = userPreferences
        )
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(@ApplicationContext context: Context): NotificationRepository {
        return NotificationRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideFieldsService(retrofit: Retrofit): FieldsService {
        return retrofit.create(FieldsService::class.java)
    }

    @Provides
    @Singleton
    fun provideFieldsRepository(
        fieldsApi: FieldsService,
        database: FieldsDatabase
    ): FieldsRepository {
        return FieldsRepositoryImpl(
            fieldsApi = fieldsApi,
            database = database
        )
    }
}
