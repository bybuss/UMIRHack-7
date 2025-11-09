package bob.colbaskin.umirhack7.auth.domain.token

import retrofit2.Response
import retrofit2.http.GET

interface RefreshTokenService {

    @GET("/refresh")
    suspend fun refresh(): Response<Unit>
}
