package bob.colbaskin.umirhack7.point_picker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.common.design_system.utils.getColors

@Composable
fun PointPickerBottomBar(
    isValid: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        contentColor = CustomTheme.colors.black,
        color = CustomTheme.colors.white
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.getColors()
            ) {
                Text("Назад", color = CustomTheme.colors.black)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                enabled = isValid,
                colors = ButtonDefaults.getColors()
            ) {
                Text(
                    "Подтвердить",
                    color = CustomTheme.colors.black
                )
            }
        }
    }
}
