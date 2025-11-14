package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun NavigationBarItemDefaults.getColors() = colors(
    selectedIconColor = CustomTheme.colors.main,
    selectedTextColor = CustomTheme.colors.main,
    indicatorColor  = CustomTheme.colors.secondary,
    unselectedIconColor = CustomTheme.colors.black,
    unselectedTextColor = CustomTheme.colors.black,
)
