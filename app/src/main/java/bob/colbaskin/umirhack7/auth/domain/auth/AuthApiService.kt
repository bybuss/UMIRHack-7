package bob.colbaskin.umirhack7.auth.domain.auth

import bob.colbaskin.umirhack7.auth.data.models.LoginBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("/login")
    suspend fun login(
        @Header("x-client-type") clientType: String = "Mobile",
        @Body body: LoginBody
    ): Response<Unit>

    @POST("/register")
    suspend fun register(
        @Header("x-client-type") clientType: String = "Mobile",
        @Body body: RegisterBody
    ): RegisterDTO
}
