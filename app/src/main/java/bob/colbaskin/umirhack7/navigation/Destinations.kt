package bob.colbaskin.umirhack7.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destinations(
    val icon: ImageVector,
    val label: String,
    val screen: Screens
) {
    MAP(
        icon = Icons.Default.Map,
        label = "Карта",
        screen = Screens.Map
    ),
    PROFILE(
        icon = Icons.Default.Person,
        label = "Профиль",
        screen = Screens.Profile
    )
}
