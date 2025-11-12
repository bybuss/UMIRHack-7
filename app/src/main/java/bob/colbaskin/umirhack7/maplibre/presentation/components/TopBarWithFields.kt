package bob.colbaskin.umirhack7.maplibre.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.LocationState
import bob.colbaskin.umirhack7.maplibre.presentation.MapLibreAction
import bob.colbaskin.umirhack7.maplibre.presentation.MapLibreState
import compose.icons.TablerIcons
import compose.icons.tablericons.Eye
import compose.icons.tablericons.EyeOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithFields(
    state: MapLibreState,
    locationState: LocationState,
    onAction: (MapLibreAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isEyeOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = locationState.cityName ?: "Неизвестно",
            modifier = Modifier.weight(1f)
        )

        when (val fieldsState = state.fieldsState) {
            is UiState.Success -> {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(
                            text = state.selectedField?.name ?: "Поля (${fieldsState.data.size})",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = "Список полей"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Все поля") },
                            onClick = {
                                onAction(MapLibreAction.ClearSelectedField)
                                expanded = false
                            },
                            trailingIcon = {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isEyeOpen) TablerIcons.Eye else TablerIcons.EyeOff,
                                        contentDescription = "Показать",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable( onClick = {
                                                onAction(MapLibreAction.ToggleFieldsVisibility)
                                                isEyeOpen = !isEyeOpen
                                            }
                                            )
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Replay,
                                        contentDescription = "Повторить",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable(onClick = {
                                                onAction(MapLibreAction.LoadFields)
                                            }
                                            )
                                    )
                                }
                            }
                        )

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        fieldsState.data.forEach { field ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = field.name,
                                            fontWeight = if (field == state.selectedField) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "Площадь: ${"%.2f".format(field.area / 10000)} га",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                onClick = {
                                    onAction(MapLibreAction.SelectField(field))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
            is UiState.Error -> {
                Icon(
                    imageVector = Icons.Filled.Replay,
                    contentDescription = "Ошибка загрузки полей",
                    tint = Color.Red,
                    modifier = Modifier.clickable(
                        onClick = { onAction(MapLibreAction.LoadFields) }
                    )
                )
            }
        }
    }
}
