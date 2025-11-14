package bob.colbaskin.umirhack7.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.common.design_system.utils.getColors
import org.ramani.compose.Circle

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = CustomTheme.colors.white,
        contentColor = CustomTheme.colors.black,
    ) {
        Destinations.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.screen::class)
            } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(destination.iconId) ,
                        contentDescription = destination.label
                    )
                },
                label = { destination.label },
                selected = selected,
                onClick = {
                    navController.navigate(destination.screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.getColors()
            )
        }
    }
}
