package bob.colbaskin.umirhack7.di

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
import bob.colbaskin.umirhack7.maplibre.data.LocationRepositoryImpl
import bob.colbaskin.umirhack7.maplibre.data.OfflineMapRepositoryImpl
import bob.colbaskin.umirhack7.maplibre.domain.LocationRepository
import bob.colbaskin.umirhack7.maplibre.domain.OfflineMapRepository
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
    ): AuthRepository {
        return AuthRepositoryImpl(
            authApi = authApi,
            userPreferences = userPreferences
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
        tokenApi: RefreshTokenService
    ): RefreshTokenRepository {
        return RefreshTokenRepositoryImpl(
            tokenApi = tokenApi
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
}
