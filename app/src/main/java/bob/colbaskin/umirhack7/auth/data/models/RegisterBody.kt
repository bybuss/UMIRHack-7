package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)
