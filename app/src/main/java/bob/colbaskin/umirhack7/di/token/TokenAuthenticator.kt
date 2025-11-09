package bob.colbaskin.umirhack7.di.token

import android.util.Log
import bob.colbaskin.umirhack7.auth.domain.token.RefreshTokenRepository
import bob.colbaskin.umirhack7.common.ApiResult
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "Auth"

class TokenAuthenticator @Inject constructor(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            Log.d(TAG, "Unauthorized. Attempting to refresh token")

            val refreshToken = tokenManager.getRefreshToken()

            if (!refreshToken.isNullOrEmpty()) {
                val refreshResult = runBlocking {
                    refreshTokenRepository.refresh(refreshToken)
                }

                if (refreshResult is ApiResult.Success) {
                    val newAccessToken = tokenManager.getAccessToken()

                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    Log.d(TAG, "Token refresh failed")
                }
            }
        }

        return null
    }
}
