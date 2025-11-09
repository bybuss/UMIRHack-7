package bob.colbaskin.umirhack7.navigation

import kotlinx.serialization.Serializable

interface Screens {
    @Serializable
    data object Home: Screens

    @Serializable
    data object SomeScreen: Screens

    @Serializable
    data object Profile: Screens

    @Serializable
    data object Welcome: Screens

    @Serializable
    data object Introduction: Screens

    @Serializable
    data object SignIn: Screens

    @Serializable
    data object SignUp: Screens
}
