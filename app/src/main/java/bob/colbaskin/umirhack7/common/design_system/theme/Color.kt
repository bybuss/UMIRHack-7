package bob.colbaskin.umirhack7.common.design_system.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


data class AppColors(
    val color: Color,
)

val LocalColors = compositionLocalOf { lightColors }

val lightColors = AppColors(
    color = Color(0xFF000000),
)

val darkColors  = AppColors(
    color = Color(0xFFFFFFFF)
)
