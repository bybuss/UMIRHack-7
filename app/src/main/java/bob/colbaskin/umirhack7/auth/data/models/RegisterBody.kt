package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val email: String,
    val password: String
)
