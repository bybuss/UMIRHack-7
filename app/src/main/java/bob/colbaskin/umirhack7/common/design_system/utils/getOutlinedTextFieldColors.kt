package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme

@Composable
fun OutlinedTextFieldDefaults.getColors() = colors(
    disabledBorderColor = Color.Transparent,
    focusedBorderColor = Color.Transparent,
    errorBorderColor = Color.Transparent,
    disabledLabelColor = CustomTheme.colors.black,
    errorLabelColor = CustomTheme.colors.red,
    focusedLabelColor = CustomTheme.colors.black,
    unfocusedLabelColor = CustomTheme.colors.black,
    disabledTrailingIconColor = CustomTheme.colors.secondaryGray,
    errorTrailingIconColor = CustomTheme.colors.red,
    focusedTrailingIconColor = CustomTheme.colors.secondaryGray,
    unfocusedTrailingIconColor = CustomTheme.colors.secondaryGray,
    disabledContainerColor = CustomTheme.colors.lightGray,
    errorContainerColor = CustomTheme.colors.lightGray,
    focusedContainerColor = CustomTheme.colors.lightGray,
    unfocusedContainerColor = CustomTheme.colors.lightGray
)
