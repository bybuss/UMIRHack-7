package bob.colbaskin.umirhack7.auth.data

import android.util.Log
import bob.colbaskin.umirhack7.auth.data.models.LoginBody
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import bob.colbaskin.umirhack7.auth.domain.auth.AuthApiService
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import jakarta.inject.Inject

private const val TAG = "Auth"

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val userPreferences: UserPreferencesRepository,
): AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): ApiResult<Unit> {
        Log.d(TAG, "Attempting login for username: $username")
        //FIXME: deleted after server is online
        userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
        return safeApiCall<TokenDTO, Unit>(
            apiCall = {
                authApi.login(
                    body = LoginBody(
                        username = username,
                        password = password
                    )
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Login successful. Saving Authenticated status")
                userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
                response
            }
        )
    }
}
