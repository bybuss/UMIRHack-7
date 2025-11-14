package bob.colbaskin.umirhack7.auth.presentation.sign_up

interface SignUpAction {
    data object SignIn : SignUpAction
    data object SignUp : SignUpAction
    data class UpdateUserName(val userName: String): SignUpAction
    data class UpdateEmail(val email: String): SignUpAction
    data class UpdatePassword(val password: String): SignUpAction
    data class UpdateFirstname(val firstName: String): SignUpAction
    data class UpdateLastName(val lastName: String): SignUpAction
}
