package bob.colbaskin.umirhack7.common.user_prefs.domain.models

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String
)
