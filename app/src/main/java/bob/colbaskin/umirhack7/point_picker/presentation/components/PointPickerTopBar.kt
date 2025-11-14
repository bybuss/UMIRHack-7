package bob.colbaskin.umirhack7.point_picker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import bob.colbaskin.umirhack7.common.design_system.theme.CustomTheme
import bob.colbaskin.umirhack7.common.design_system.utils.getColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointPickerTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Выбор точки измерения",
                style = MaterialTheme.typography.titleMedium,
                color = CustomTheme.colors.black
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = CustomTheme.colors.black
                )
            }
        },
        colors = TopAppBarDefaults.getColors()
    )
}
