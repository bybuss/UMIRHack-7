package bob.colbaskin.umirhack7.navigation.graphs

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import bob.colbaskin.umirhack7.auth.presentation.sign_in.SignInScreenRoot
import bob.colbaskin.umirhack7.auth.presentation.sign_up.SignUpScreenRoot
import bob.colbaskin.umirhack7.common.user_prefs.data.models.OnboardingConfig
import bob.colbaskin.umirhack7.maplibre.presentation.MapLibreScreenRoot
import bob.colbaskin.umirhack7.navigation.Screens
import bob.colbaskin.umirhack7.navigation.animatedTransition
import bob.colbaskin.umirhack7.onboarding.presentation.IntroductionScreen
import bob.colbaskin.umirhack7.onboarding.presentation.WelcomeScreen
import bob.colbaskin.umirhack7.profile.presentation.ProfileScreenRoot
import bob.colbaskin.umirhack7.soil_analyze.presentation.SoilAnalyzeScreenRoot


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
        startDestination = Screens.Map
    ) {
        animatedTransition<Screens.Map> {
            MapLibreScreenRoot(navController)
        }
        animatedTransition<Screens.Profile> {
            ProfileScreenRoot()
        }
    }
}

fun NavGraphBuilder.detailedGraph(
    navController: NavHostController,
) {
    navigation<Graphs.Detailed>(
        startDestination = Screens.SoilAnalyze(id = 1)
    ) {
        animatedTransition<Screens.SoilAnalyze> {
            SoilAnalyzeScreenRoot(navController)
        }
    }
}


private fun getStartDestination(status: OnboardingConfig) = when (status) {
    OnboardingConfig.NOT_STARTED -> Screens.Welcome
    OnboardingConfig.IN_PROGRESS -> Screens.Introduction
    OnboardingConfig.COMPLETED -> Screens.SignIn
    else -> Screens.Welcome
}
