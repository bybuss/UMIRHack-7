package bob.colbaskin.umirhack7.auth.presentation.sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.utils.getColors
import bob.colbaskin.umirhack7.common.design_system.utils.getTextButtonColors
import bob.colbaskin.umirhack7.navigation.Screens
import bob.colbaskin.umirhack7.navigation.graphs.Graphs
import kotlinx.coroutines.launch

@Composable
fun SignUpScreenRoot(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val authState = state.authState
    val scope = rememberCoroutineScope()

    SignUpScreen(
        state = state,
        onAction = { action ->
            when (action) {
                SignUpAction.SignUp -> {
                    when (authState) {
                        is UiState.Success -> { navController.navigate(Graphs.Main) }
                        is UiState.Error -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    authState.title,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                        else -> {}
                    }
                }
                SignUpAction.SignIn -> { navController.navigate(Screens.SignIn) }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpState,
    onAction: (SignUpAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    var showPassword by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            Text(
                text = "Регистрация",
            )
            Column {
                OutlinedTextField(
                    value = state.userName,
                    onValueChange = { onAction(SignUpAction.UpdateEmail(it)) },
                    label = { Text("Юзернейм") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.getColors()
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onAction(SignUpAction.UpdateEmail(it)) },
                    label = { Text("Почта") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = !state.isEmailValid,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.getColors()
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onAction(SignUpAction.UpdatePassword(it)) },
                    label = { Text("Пароль") },
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = if (showPassword) "Hide password"
                                else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.getColors()
                )
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = { onAction(SignUpAction.UpdateEmail(it)) },
                    label = { Text("Имя") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.getColors()
                )
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = { onAction(SignUpAction.UpdateEmail(it)) },
                    label = { Text("Фамилия") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.getColors()
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { onAction(SignUpAction.SignUp) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.getColors()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Зарегистрироваться")
                    }
                }
                TextButton(
                    onClick = { onAction(SignUpAction.SignIn) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.getTextButtonColors()
                ) {
                    Text("Уже есть аккаунт? Войти")
                }
            }
        }
    }
}
