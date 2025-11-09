package bob.colbaskin.umirhack7.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destinations(
    val icon: ImageVector,
    val label: String,
    val screen: Screens
) {
    HOME(
        icon = Icons.Default.Home,
        label = "Home",
        screen = Screens.Home
    ),
    SOME_SCREEN(
        icon = Icons.Default.Screenshot,
        label = "SomeScreen",
        screen = Screens.SomeScreen
    ),
    PROFILE(
        icon = Icons.Default.Person,
        label = "Profile",
        screen = Screens.Profile
    )
}
