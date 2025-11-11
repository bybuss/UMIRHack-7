package bob.colbaskin.umirhack7.di.token

import android.util.Log
import bob.colbaskin.umirhack7.auth.domain.token.RefreshTokenRepository
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.profile.domain.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "Auth"

class TokenAuthenticator @Inject constructor(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager,
    private val scope: CoroutineScope
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("X-Retry-After-Refresh") == "true") {
            Log.d(TAG, "Already attempted token refresh, failing")
            return null
        }

        if (response.code == 401) {
            Log.d(TAG, "Unauthorized. Attempting to refresh token")

            val refreshToken = tokenManager.getRefreshToken()

            if (!refreshToken.isNullOrEmpty()) {
                val refreshResult = runBlocking {
                    refreshTokenRepository.refresh(refreshToken)
                }

                when (refreshResult) {
                    is ApiResult.Success -> {
                        val newAccessToken = tokenManager.getAccessToken()
                        Log.d(TAG, "Token refresh successful, retrying request")

                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .header("X-Retry-After-Refresh", "true")
                            .build()
                    }
                    is ApiResult.Error -> {
                        Log.d(TAG, "Token refresh failed with error: ${refreshResult.text}")
                        scope.launch {
                            profileRepository.logout()
                        }
                    }
                }
            } else {
                Log.d(TAG, "No refresh token available")
            }
        }

        return null
    }
}
