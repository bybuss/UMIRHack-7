package bob.colbaskin.umirhack7.auth.domain.token

import bob.colbaskin.umirhack7.common.ApiResult

interface RefreshTokenRepository {

    suspend fun refresh(refreshToken: String): ApiResult<Unit>
}
