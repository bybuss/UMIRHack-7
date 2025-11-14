package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun MenuDefaults.getColors() = itemColors(
    textColor = CustomTheme.colors.black,
    leadingIconColor = CustomTheme.colors.black,
    trailingIconColor = CustomTheme.colors.black,
    disabledTextColor = CustomTheme.colors.black,
    disabledLeadingIconColor = CustomTheme.colors.black,
    disabledTrailingIconColor = CustomTheme.colors.black
)
