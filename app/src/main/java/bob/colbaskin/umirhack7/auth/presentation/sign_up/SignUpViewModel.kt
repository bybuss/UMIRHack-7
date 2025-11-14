package bob.colbaskin.umirhack7.auth.presentation.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(SignUpState())
        private set

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.SignUp -> register()
            is SignUpAction.UpdateUserName -> updateUserName(action.userName)
            is SignUpAction.UpdateEmail -> updateEmail(action.email)
            is SignUpAction.UpdatePassword -> updatePassword(action.password)
            is SignUpAction.UpdateFirstname -> updateFirstName(action.firstName)
            is SignUpAction.UpdateLastName -> updateLastName(action.lastName)
            else -> Unit
        }
    }

    private fun register() {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            val response = authRepository.register(
                username = state.userName,
                email = state.email,
                password = state.password,
                firstName = state.firstName,
                lastName = state.lastName
            ).toUiState()

            state = state.copy(
                authState = UiState.Success(Unit)/* response */,
                isLoading = false
            )
        }
    }

    private fun updateUserName(userName: String) {
        state = state.copy(userName = userName)
    }

    private fun updateEmail(email: String) {
        state = state.copy(email = email)
    }

    private fun updatePassword(password: String) {
        state = state.copy(password = password)
    }

    private fun updateFirstName(firstName: String) {
        state = state.copy(firstName = firstName)
    }

    private fun updateLastName(lastName: String) {
        state = state.copy(lastName = lastName)
    }
}
