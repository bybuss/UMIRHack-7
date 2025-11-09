package bob.colbaskin.umirhack7.auth.presentation.sign_up

import android.util.Patterns
import bob.colbaskin.umirhack7.common.UiState

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authState: UiState<Unit> = UiState.Loading
) {
    val isEmailValid: Boolean
        get() = email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
