package bob.colbaskin.umirhack7.auth.domain.auth

import bob.colbaskin.umirhack7.auth.data.models.LoginBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterBody
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): TokenDTO

    @POST("/api/auth/register")
    suspend fun register(
        @Body body: RegisterBody
    ): TokenDTO
}
