package bob.colbaskin.umirhack7.common.design_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bob.colbaskin.umirhack7.R

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = {data ->
            CustomSnackbarContent(snackbarData = data)
        }
    )
}

@Composable
private fun CustomSnackbarContent(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(16.dp),
        // color = CustomTheme.colors.snackbarContainer
    ) {
        Row(
            modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = "info",
                modifier = Modifier.size(20.dp),
                // tint = CustomTheme.colors.mainText
            )
            Text(
                text = snackbarData.visuals.message,
                modifier = Modifier.weight(1f),
                // color = CustomTheme.colors.mainText,
                textAlign = TextAlign.Start
            )
        }
    }
}
