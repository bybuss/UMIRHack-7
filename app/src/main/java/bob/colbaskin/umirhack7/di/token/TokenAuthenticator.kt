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
import javax.inject.Provider

private const val TAG = "Auth"

class TokenAuthenticator @Inject constructor(
    private val refreshTokenRepository: Provider<RefreshTokenRepository>,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            Log.d(TAG, "Unauthorized. Attempting to refresh token")

            val refreshResult = runBlocking {
                refreshTokenRepository.get().refresh()
            }

            if (refreshResult is ApiResult.Success<*>) {
                Log.d(TAG, "Refresh token updated!")
                return response.request.newBuilder().build()
            } else {
                Log.d(TAG, "Refresh token failed")
            }
        }

        return null
    }
}
