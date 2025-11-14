package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun ProgressIndicatorDefaults.getColors() = colors(
    indicatorColor = CustomTheme.colors.main,
    trackColor = CustomTheme.colors.lightGray
)
