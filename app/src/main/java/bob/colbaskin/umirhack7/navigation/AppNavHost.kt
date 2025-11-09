package bob.colbaskin.umirhack7.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.common.design_system.CustomSnackbarHost
import bob.colbaskin.umirhack7.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.umirhack7.common.user_prefs.data.models.UserPreferences
import bob.colbaskin.umirhack7.navigation.graphs.Graphs
import bob.colbaskin.umirhack7.navigation.graphs.mainGraph
import bob.colbaskin.umirhack7.navigation.graphs.onboardingGraph

@Composable
fun AppNavHost(uiState: UiState.Success<UserPreferences>) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentDestination = currentBackStack?.destination?.route
    val isTrueScreen: Boolean = Destinations.entries.any { destination ->
        val screen = destination.screen
        val screenClassName = screen::class.simpleName
        val currentScreenName = currentDestination?.substringAfterLast(".") ?: ""
        currentScreenName == screenClassName
    }

    Scaffold(
        snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Log.d("LOG", "destination: $currentDestination")
            Log.d("LOG", "trueScreen: $isTrueScreen")
            AnimatedVisibility(
                visible = isTrueScreen
            ) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            startDestination = getStartDestination(uiState.data.authStatus),
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        ) {
            onboardingGraph(
                navController = navController,
                onboardingStatus = uiState.data.onboardingStatus,
                snackbarHostState = snackbarHostState
            )
            mainGraph(navController, snackbarHostState)
        }
    }
}

private fun getStartDestination(status: AuthConfig) =
    when (status) {
        AuthConfig.AUTHENTICATED -> Graphs.Main
        else -> Graphs.Onboarding
    }