package bob.colbaskin.umirhack7.auth.data

import android.util.Log
import bob.colbaskin.umirhack7.auth.data.models.CreateOrganizationBody
import bob.colbaskin.umirhack7.auth.data.models.GetMeFullDTO
import bob.colbaskin.umirhack7.auth.data.models.LoginBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterDTO
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import bob.colbaskin.umirhack7.auth.data.models.toDomain
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
                Log.d(TAG, "Login successful. Saving Authenticated status + user")
                userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
                tokenManager.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
                getMeAndSave()
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

    override suspend fun getMeAndSave(): ApiResult<Unit> {
        Log.d(TAG, "Attempting getMe")
        return safeApiCall<GetMeFullDTO, Unit>(
            apiCall = { authApi.getMe() },
            successHandler = { response ->
                val user = response.toDomain()
                Log.d(TAG, "getMe successful. Saving user=$user")
                userPreferences.saveUserInfo(
                    userId = user.userId,
                    username = user.username,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName
                )
            }
        )
    }

    override suspend fun createOrganization(
        userId: String,
        name: String
    ): ApiResult<Unit> {
        Log.d(TAG, "Attempting create org with name=$name")
        return safeApiCall<Unit, Unit>(
            apiCall = {
                authApi.createOrganization(
                    userId = userId,
                    body = CreateOrganizationBody(
                        name = name
                    )
                )
            },
            successHandler = { response ->
                Log.d(TAG, "Create org successful.")
            }
        )
    }
}
