package bob.colbaskin.umirhack7.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.umirhack7.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Logout -> logout()
            else -> Unit
        }
    }

    private fun logout() {
        viewModelScope.launch {
            profileRepository.logout()
        }
    }
}