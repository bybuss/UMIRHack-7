package bob.colbaskin.umirhack7.auth.presentation.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.auth.domain.auth.AuthRepository
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.toUiState
import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferencesRepository,

): ViewModel() {

    var state by mutableStateOf(SignUpState())
        private set

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.SignUp -> register()
            is SignUpAction.UpdateEmail -> updateEmail(action.email)
            is SignUpAction.UpdatePassword -> updatePassword(action.password)
            else -> Unit
        }
    }

    private fun register() {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            val response = authRepository.register(
                email = state.email,
                password = state.password
            ).toUiState()

            state = state.copy(
                authState = UiState.Success(Unit)/* response */,
                isLoading = false
            )
            userPreferences.saveAuthStatus(AuthConfig.AUTHENTICATED)
        }
    }

    private fun updateEmail(email: String) {
        state = state.copy(email = email)
    }

    private fun updatePassword(password: String) {
        state = state.copy(password = password)
    }
}
