package bob.colbaskin.umirhack7.auth.data.models

import bob.colbaskin.umirhack7.auth.domain.models.Token
import bob.colbaskin.umirhack7.common.user_prefs.domain.models.User

fun TokenDTO.toDomain(): Token {
    return Token(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}

fun GetMeFullDTO.toDomain(): User {
    return User(
        userId = this.user.sid,
        username = this.user.username,
        email = this.user.email,
        firstName = this.user.givenName,
        lastName = this.user.familyName
    )
}
