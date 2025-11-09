package bob.colbaskin.umirhack7.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    
    ProfileScreen(
        state = state,
        onAction = { action ->
            when (action) {
                else -> Unit
            }
            viewModel.onAction(action)
        }
    ) 
}

@Composable
private fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Button (
            onClick = {
                onAction(ProfileAction.Logout)
            }
        ) {
            Text("Выйти")
        }
    }
}