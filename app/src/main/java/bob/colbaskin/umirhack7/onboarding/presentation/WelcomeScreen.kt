package bob.colbaskin.umirhack7.onboarding.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.umirhack7.R
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.common.design_system.utils.getColors

@Composable
fun WelcomeScreen(
    onNextScreen: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    Welcome(onNextScreen, viewModel::action)
}

@Composable
fun Welcome(
    onNextScreen: () -> Unit,
    dispatch: (OnBoardingAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp,
                bottom = 32.dp,
                start = 16.dp,
                end = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.wheat),
            contentDescription = "Колосочек"
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            text = "Agro Hub",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = CustomTheme.colors.black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ваш цифровой помощник для умного севооборота и планирования урожая. Повышаем плодородие почвы и вашу прибыль.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = CustomTheme.colors.black
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                dispatch(OnBoardingAction.OnboardingInProgress)
                onNextScreen()
            },
            colors = ButtonDefaults.getColors()
        ) {
            Text(
                "Начать",
                color = CustomTheme.colors.black
            )
        }
    }
}
