package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDTO(
    val message: String,
    val userId: String
)
