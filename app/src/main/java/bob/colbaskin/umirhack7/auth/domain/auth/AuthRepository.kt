package bob.colbaskin.umirhack7.auth.domain.auth

import bob.colbaskin.umirhack7.common.ApiResult

interface AuthRepository {

    suspend fun login(username: String, password: String): ApiResult<Unit>

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): ApiResult<Unit>
}
