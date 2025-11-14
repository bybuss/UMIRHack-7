package bob.colbaskin.umirhack7.common.design_system.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val main: Color,
    val secondary: Color,
    val salad: Color,
    val black: Color,
    val white: Color,
    val red: Color,
    val gray: Color,
    val secondaryGray: Color,
    val lightGray: Color,
    val orange: Color,
    val blue: Color,
    val purple: Color,
    val cian: Color,
)

val LocalColors = compositionLocalOf { lightColors }

val lightColors = AppColors(
    main = Color(0xFF4EC545),
    secondary = Color(0xFFEEF6ED),
    salad = Color(0xFF3AAA35),
    black = Color.Black,
    white = Color.White,
    red = Color(0xFFCF3F56),
    gray = Color(0xFF879198),
    secondaryGray = Color(0xFFB3B3B3),
    lightGray = Color(0xFFF6F6F6),
    orange = Color(0xFFFA7F19),
    blue = Color(0xFF1B87E6),
    purple = Color(0xFF7652F0),
    cian = Color(0xFF16B6C4),
)

val darkColors  = AppColors(
    main = Color(0xFF4EC545),
    secondary = Color(0xFFEEF6ED),
    salad = Color(0xFF3AAA35),
    black = Color.Black,
    white = Color.White,
    red = Color(0xFFCF3F56),
    gray = Color(0xFF879198),
    secondaryGray = Color(0xFFB3B3B3),
    lightGray = Color(0xFFF6F6F6),
    orange = Color(0xFFFA7F19),
    blue = Color(0xFF1B87E6),
    purple = Color(0xFF7652F0),
    cian = Color(0xFF16B6C4),
)
