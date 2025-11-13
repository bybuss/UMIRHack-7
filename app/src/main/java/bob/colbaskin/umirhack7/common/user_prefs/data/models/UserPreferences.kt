package bob.colbaskin.umirhack7.common.user_prefs.data.models

import bob.colbaskin.umirhack7.datastore.AuthStatus
import bob.colbaskin.umirhack7.datastore.OnboardingStatus
import bob.colbaskin.umirhack7.datastore.UserPreferencesProto

data class UserPreferences(
    val onboardingStatus: OnboardingConfig,
    val authStatus: AuthConfig,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

fun UserPreferencesProto.toData(): UserPreferences {
    return UserPreferences(
        onboardingStatus = when (this.onboardingStatus) {
            OnboardingStatus.NOT_STARTED -> OnboardingConfig.NOT_STARTED
            OnboardingStatus.IN_PROGRESS -> OnboardingConfig.IN_PROGRESS
            OnboardingStatus.COMPLETED -> OnboardingConfig.COMPLETED
            OnboardingStatus.UNRECOGNIZED, null -> OnboardingConfig.NOT_STARTED
        },
        authStatus = when (this.authStatus) {
            AuthStatus.AUTHENTICATED -> AuthConfig.AUTHENTICATED
            AuthStatus.NOT_AUTHENTICATED -> AuthConfig.NOT_AUTHENTICATED
            AuthStatus.UNRECOGNIZED, null -> AuthConfig.NOT_AUTHENTICATED
        },
        username = this.username,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
    )
}
