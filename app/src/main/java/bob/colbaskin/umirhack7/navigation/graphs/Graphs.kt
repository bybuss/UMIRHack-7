package bob.colbaskin.umirhack7.navigation.graphs

import kotlinx.serialization.Serializable

interface Graphs {

    @Serializable
    data object Main: Graphs

    @Serializable
    data object Onboarding: Graphs

    @Serializable
    data object Detailed: Graphs
}
