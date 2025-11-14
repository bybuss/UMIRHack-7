package bob.colbaskin.umirhack7.auth.data

import android.util.Log
import bob.colbaskin.umirhack7.auth.data.models.LoginBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterDTO
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import bob.colbaskin.umirhack7.auth.domain.auth.AuthApiService
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import bob.colbaskin.umirhack7.di.token.TokenManager
import jakarta.inject.Inject

private const val TAG = "Auth"

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val userPreferences: UserPreferencesRepository,
    private val tokenManager: TokenManager
): AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): ApiResult<Unit> {
        Log.d(TAG, "Attempting login for username: $username")
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
                tokenManager.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
                response
            }
        )
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): ApiResult<Unit> {
        Log.d(TAG, "Attempting register for username: $username")
        return safeApiCall<RegisterDTO, Unit>(
            apiCall = {
                authApi.register(
                    body = RegisterBody(
                        username = username,
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName
                    )
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Register successful.")
                userPreferences.saveUserInfo(
                    userId = response.userId,
                    username = username,
                    email = email,
                    firstName = firstName,
                    lastName = lastName
                )
                login(
                    username = username,
                    password = password
                )
                response
            }
        )
    }
}
