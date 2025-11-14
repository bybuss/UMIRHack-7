package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun IconButtonDefaults.getColors() = iconButtonColors(
    containerColor = Color.Transparent,
    contentColor = CustomTheme.colors.white,
    disabledContainerColor = Color.Transparent,
    disabledContentColor = CustomTheme.colors.white
)
