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
    disabledLabelColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    errorLabelColor = CustomTheme.colors.red,
    focusedLabelColor = CustomTheme.colors.black,
    unfocusedLabelColor = CustomTheme.colors.black,
    disabledTrailingIconColor = CustomTheme.colors.gray,
    errorTrailingIconColor = CustomTheme.colors.red,
    focusedTrailingIconColor = CustomTheme.colors.gray,
    unfocusedTrailingIconColor = CustomTheme.colors.gray,
    disabledContainerColor = CustomTheme.colors.lightGray,
    errorContainerColor = CustomTheme.colors.lightGray,
    focusedContainerColor = CustomTheme.colors.lightGray,
    unfocusedContainerColor = CustomTheme.colors.lightGray,
    errorLeadingIconColor = CustomTheme.colors.black,
    disabledLeadingIconColor = CustomTheme.colors.black,
    focusedLeadingIconColor = CustomTheme.colors.black,
    unfocusedLeadingIconColor = CustomTheme.colors.black,
)
