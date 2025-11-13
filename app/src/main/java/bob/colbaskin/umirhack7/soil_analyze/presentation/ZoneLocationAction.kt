package bob.colbaskin.umirhack7.soil_analyze.presentation

sealed interface ZoneLocationAction {
    object UseCurrentLocation : ZoneLocationAction
    object OpenMap : ZoneLocationAction
    object ShowOptions : ZoneLocationAction
    object HideOptions : ZoneLocationAction
}
