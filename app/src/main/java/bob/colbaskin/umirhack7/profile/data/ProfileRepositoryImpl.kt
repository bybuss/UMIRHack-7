package bob.colbaskin.umirhack7.profile.data

import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.umirhack7.di.token.TokenManager
import bob.colbaskin.umirhack7.profile.domain.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferencesRepository,
): ProfileRepository {
    override suspend fun logout() {
        tokenManager.cleatTokens()
        userPreferences.saveAuthStatus(AuthConfig.NOT_AUTHENTICATED)
    }
}
