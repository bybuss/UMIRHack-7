package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val username: String,
    val password: String
)
