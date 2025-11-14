package bob.colbaskin.umirhack7.auth.presentation.sign_in

import bob.colbaskin.umirhack7.common.UiState

data class SignInState(
    val userName: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authState: UiState<Unit> = UiState.Loading
) {
    val isFormValid: Boolean
        get() = userName.isNotEmpty() &&
                password.isNotEmpty() &&
                !isLoading
}
