package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun ButtonDefaults.getTextButtonColors() = textButtonColors(
    contentColor = CustomTheme.colors.main,
    disabledContentColor = CustomTheme.colors.gray
)
