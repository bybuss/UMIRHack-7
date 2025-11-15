package bob.colbaskin.umirhack7.di.org

import android.util.Log
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.common.ApiResult
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Provider

private const val TAG = "OrganizationInterceptor"

class OrganizationInterceptor @Inject constructor(
    private val authRepository: Provider<AuthRepository>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath == "/api/organization/create") {
            return chain.proceed(request)
        }

        val response = chain.proceed(request)

        if (response.code == 400 && request.header("X-Retry-After-Create-Org") != "true") {
            val responseBody = response.peekBody(1024 * 1024)
            val errorBody = responseBody.string()

            Log.d(TAG, "Received 400 error, checking error body: $errorBody")

            try {
                val jsonError = JSONObject(errorBody)
                val errorMessage = jsonError.optString("message", "")

                if (errorMessage == "Organization not found for user") {
                    Log.d(TAG, "Organization not found error detected, creating organization")

                    response.close()

                    val userId = request.header("x-user-id")

                    if (!userId.isNullOrEmpty()) {
                        val createOrgResult = runBlocking {
                            authRepository.get().createOrganization(userId, "Default Organization")
                        }

                        when (createOrgResult) {
                            is ApiResult.Success -> {
                                Log.d(TAG, "Organization created successfully, retrying original request")

                                val newRequest = request.newBuilder()
                                    .header("X-Retry-After-Create-Org", "true")
                                    .build()

                                return chain.proceed(newRequest)
                            }
                            is ApiResult.Error -> {
                                Log.d(TAG, "Failed to create organization: ${createOrgResult.text}")
                                return response
                            }
                        }
                    } else {
                        Log.d(TAG, "No user-id header found, cannot create organization")
                    }
                } else {
                    Log.d(TAG, "400 error is not related to organization, skipping")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Failed to parse error body: ${e.message}")
            }
        }

        return response
    }
}
