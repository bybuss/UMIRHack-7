package bob.colbaskin.umirhack7.auth.domain.token

import bob.colbaskin.umirhack7.auth.data.models.RefreshBody
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenService {

    @POST("/api/auth/refresh")
    suspend fun refresh(
        @Body body: RefreshBody
    ): TokenDTO
}
