package bob.colbaskin.umirhack7.common.user_prefs.domain

import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.data.models.OnboardingConfig
import bob.colbaskin.umirhack7.common.user_prefs.data.models.UserPreferences
import bob.colbaskin.umirhack7.common.user_prefs.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    fun getUserPreferences(): Flow<UserPreferences>

    suspend fun saveAuthStatus(status: AuthConfig)

    suspend fun saveOnboardingStatus(status: OnboardingConfig)

    fun getUser(): Flow<User>

    suspend fun saveUserInfo(
        username: String,
        email: String,
        firstName: String,
        lastName: String
    )

    suspend fun clearUser()
}