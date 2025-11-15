package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class GetMeFullDTO(
    val message: String,
    val user: GetMeDTO
)
