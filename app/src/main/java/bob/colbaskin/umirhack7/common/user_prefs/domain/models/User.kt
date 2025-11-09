package bob.colbaskin.umirhack7.common.user_prefs.domain.models

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val avatarUrl: String? = null
)
