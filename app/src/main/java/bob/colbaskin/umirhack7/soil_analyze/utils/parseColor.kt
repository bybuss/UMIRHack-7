package bob.colbaskin.umirhack7.soil_analyze.utils

import androidx.compose.ui.graphics.Color

fun String.parseColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}
