package bob.colbaskin.umirhack7.auth.presentation.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(SignInState())
        private set

    fun onAction(action: SignInAction) {
        when (action) {
            is SignInAction.UpdateUserName -> updateUserName(action.userName)
            is SignInAction.UpdatePassword -> updatePassword(action.password)
            SignInAction.SignIn -> login()
            else -> Unit
        }
    }

    private fun login() {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            val response = authRepository.login(
                username = state.userName,
                password = state.password
            ).toUiState()

            state = state.copy(
                authState = response,
                isLoading = false
            )
        }
    }

    private fun updateUserName(userName: String) {
        state = state.copy(userName = userName)
    }

    private fun updatePassword(password: String) {
        state = state.copy(password = password)
    }
}
