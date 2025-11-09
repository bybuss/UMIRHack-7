package bob.colbaskin.umirhack7.navigation.graphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import bob.colbaskin.umirhack7.auth.presentation.sign_in.SignInScreenRoot
import bob.colbaskin.umirhack7.auth.presentation.sign_up.SignUpScreenRoot
import bob.colbaskin.umirhack7.common.user_prefs.data.models.OnboardingConfig
import bob.colbaskin.umirhack7.navigation.Screens
import bob.colbaskin.umirhack7.navigation.animatedTransition
import bob.colbaskin.umirhack7.onboarding.presentation.IntroductionScreen
import bob.colbaskin.umirhack7.onboarding.presentation.WelcomeScreen
import bob.colbaskin.umirhack7.profile.ProfileScreen


fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onboardingStatus: OnboardingConfig,
    snackbarHostState: SnackbarHostState
) {
    navigation<Graphs.Onboarding>(
        startDestination = getStartDestination(onboardingStatus)
    ) {
        animatedTransition<Screens.Welcome> {
            WelcomeScreen (
                onNextScreen = { navController.navigate(Screens.Introduction) {
                    popUpTo(Screens.Welcome) { inclusive = true }
                }}
            )
        }
        animatedTransition<Screens.Introduction> {
            IntroductionScreen (
                onNextScreen = { navController.navigate(Screens.SignIn) {
                    popUpTo(Screens.Introduction) { inclusive = true }
                }}
            )
        }
        animatedTransition<Screens.SignIn> {
            SignInScreenRoot(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
        animatedTransition<Screens.SignUp> {
            SignUpScreenRoot(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    navigation<Graphs.Main>(
        startDestination = Screens.Home
    ) {
        animatedTransition<Screens.Home> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Home Screen")
            }
        }
        animatedTransition<Screens.SomeScreen> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Some Screen")
            }
        }
        animatedTransition<Screens.Profile> {
            ProfileScreen()
        }
    }
}


private fun getStartDestination(status: OnboardingConfig) = when (status) {
    OnboardingConfig.NOT_STARTED -> Screens.Welcome
    OnboardingConfig.IN_PROGRESS -> Screens.Introduction
    OnboardingConfig.COMPLETED -> Screens.SignIn
    else -> Screens.Welcome
}
