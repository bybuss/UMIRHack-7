package bob.colbaskin.umirhack7.auth.domain.auth

import bob.colbaskin.umirhack7.common.ApiResult

interface AuthRepository {

    suspend fun login(email: String, password: String): ApiResult<Unit>

    suspend fun register(email: String, password: String): ApiResult<Unit>
}
