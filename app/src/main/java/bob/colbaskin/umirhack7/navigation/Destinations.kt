package bob.colbaskin.umirhack7.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destinations(
    val icon: ImageVector,
    val label: String,
    val screen: Screens
) {
    CALCULATOR(
        icon = Icons.Default.Calculate,
        label = "Сalculator",
        screen = Screens.Calculator
    ),
    MAP(
        icon = Icons.Default.Map,
        label = "Map",
        screen = Screens.Map
    ),
    PROFILE(
        icon = Icons.Default.Person,
        label = "Profile",
        screen = Screens.Profile
    )
}
