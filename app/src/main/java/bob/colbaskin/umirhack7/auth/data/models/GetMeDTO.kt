package bob.colbaskin.umirhack7.auth.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMeDTO(
    val sid: String,
    val username: String,
    val email: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String
)
