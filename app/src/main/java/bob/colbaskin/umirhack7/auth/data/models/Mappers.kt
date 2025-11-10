package bob.colbaskin.umirhack7.auth.data.models

import bob.colbaskin.umirhack7.auth.domain.models.Token

fun TokenDTO.toDomain(): Token {
    return Token(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}