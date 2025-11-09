package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val email: String,
    val password: String,
    val system: String = "Mobile"
)
