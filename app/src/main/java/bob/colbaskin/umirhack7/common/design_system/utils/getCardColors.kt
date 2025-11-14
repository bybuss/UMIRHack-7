package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun CardDefaults.getColors() = cardColors(
    containerColor = CustomTheme.colors.white,
    contentColor = CustomTheme.colors.black,
    disabledContainerColor = CustomTheme.colors.lightGray,
    disabledContentColor = CustomTheme.colors.gray
)
