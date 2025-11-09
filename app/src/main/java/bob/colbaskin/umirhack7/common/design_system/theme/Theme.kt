package bob.colbaskin.umirhack7.common.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun HackathonTemplateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors else lightColors

    CompositionLocalProvider(
        LocalColors provides colors,
        content = content
    )
}

object CustomTheme {
    val colors: AppColors
        @Composable get() = LocalColors.current
}
