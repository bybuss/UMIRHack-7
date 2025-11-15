package bob.colbaskin.umirhack7.auth.domain.auth

import bob.colbaskin.umirhack7.auth.data.models.CreateOrganizationBody
import bob.colbaskin.umirhack7.auth.data.models.GetMeFullDTO
import bob.colbaskin.umirhack7.auth.data.models.LoginBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterBody
import bob.colbaskin.umirhack7.auth.data.models.RegisterDTO
import bob.colbaskin.umirhack7.auth.data.models.TokenDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {

    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): TokenDTO

    @POST("/api/auth/register")
    suspend fun register(
        @Body body: RegisterBody
    ): RegisterDTO

    @GET("/api/gate/me/profile")
    suspend fun getMe(): GetMeFullDTO

    @PUT("/api/organization/create")
    suspend fun createOrganization(
        @Header("x-user-id") userId: String,
        @Body body: CreateOrganizationBody
    ): Response<Unit>
}
