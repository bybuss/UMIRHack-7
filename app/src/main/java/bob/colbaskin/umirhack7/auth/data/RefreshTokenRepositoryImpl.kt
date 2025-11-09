package bob.colbaskin.umirhack7.auth.data

import android.util.Log
import bob.colbaskin.umirhack7.auth.data.models.RefreshBody
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import bob.colbaskin.umirhack7.auth.domain.token.RefreshTokenRepository
import bob.colbaskin.umirhack7.auth.domain.token.RefreshTokenService
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import bob.colbaskin.umirhack7.di.token.TokenManager
import jakarta.inject.Inject

private const val TAG = "Auth"

class RefreshTokenRepositoryImpl @Inject constructor(
    private val refreshTokenApi: RefreshTokenService,
    private val tokenManager: TokenManager
): RefreshTokenRepository {

    override suspend fun refresh(refreshToken: String): ApiResult<Unit> {
        Log.d(TAG, "Refreshing access token")
        return safeApiCall<TokenDTO, Unit>(
            apiCall = {
                refreshTokenApi.refresh(
                    body = RefreshBody(
                        refreshToken = refreshToken
                    )
                )
            },
            successHandler = {  response ->
                Log.d(TAG, "Token refresh successful. Saving new tokens")
                tokenManager.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }
        )
    }
}
