package bob.colbaskin.umirhack7.navigation

import bob.colbaskin.umirhack7.R
enum class Destinations(
    val iconId: Int,
    val label: String,
    val screen: Screens
) {
    MAP(
        iconId = R.drawable.polygon,
        label = "Карта",
        screen = Screens.Map
    ),
    PROFILE(
        iconId = R.drawable.person,
        label = "Профиль",
        screen = Screens.Profile
    )
}
