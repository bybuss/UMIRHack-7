package bob.colbaskin.umirhack7.auth.presentation.sign_in

sealed interface SignInAction {
    data object SignIn : SignInAction
    data class UpdateUserName(val userName: String): SignInAction
    data class UpdatePassword(val password: String): SignInAction
    data object  SignUp: SignInAction
}
