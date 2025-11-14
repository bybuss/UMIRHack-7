package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun TopAppBarDefaults.getColors() = topAppBarColors(
    containerColor = CustomTheme.colors.white,
    titleContentColor = CustomTheme.colors.black,
    actionIconContentColor = CustomTheme.colors.black,
    navigationIconContentColor = CustomTheme.colors.black
)
