package bob.colbaskin.umirhack7.auth.domain.models

data class Token(
    val accessToken: String,
    val refreshToken: String
)
