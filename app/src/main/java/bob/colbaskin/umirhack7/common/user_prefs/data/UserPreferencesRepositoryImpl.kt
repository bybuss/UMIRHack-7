package bob.colbaskin.umirhack7.common.user_prefs.data

import bob.colbaskin.umirhack7.common.user_prefs.data.datastore.UserDataStore
import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.data.models.OnboardingConfig
import bob.colbaskin.umirhack7.common.user_prefs.data.models.UserPreferences
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.umirhack7.common.user_prefs.domain.models.User
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore
): UserPreferencesRepository {

    override fun getUserPreferences(): Flow<UserPreferences> = dataStore.getUserPreferences()

    override suspend fun saveAuthStatus(status: AuthConfig) = dataStore.saveAuthStatus(status)

    override suspend fun saveOnboardingStatus(status: OnboardingConfig)
            = dataStore.saveOnboardingStatus(status)

    override fun getUser(): Flow<User> = getUserPreferences()
        .map { userPreferences ->
            User(
                userId = userPreferences.userId,
                username = userPreferences.username,
                email = userPreferences.email,
                firstName = userPreferences.firstName,
                lastName = userPreferences.lastName
            )
        }

    override suspend fun saveUserInfo(
        userId: String,
        username: String,
        email: String,
        firstName: String,
        lastName: String
    ) = dataStore.saveUserInfo(
        userId = userId,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName
    )

    override suspend fun clearUser() = dataStore.clearUser()
}
