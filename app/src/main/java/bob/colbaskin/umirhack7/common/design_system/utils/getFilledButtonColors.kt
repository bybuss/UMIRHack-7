package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun ButtonDefaults.getColors(isSecondary: Boolean = false) = buttonColors(
    containerColor = if (isSecondary) CustomTheme.colors.main else CustomTheme.colors.secondary,
    contentColor = CustomTheme.colors.white,
    disabledContainerColor = CustomTheme.colors.gray,
    disabledContentColor = CustomTheme.colors.white
)
