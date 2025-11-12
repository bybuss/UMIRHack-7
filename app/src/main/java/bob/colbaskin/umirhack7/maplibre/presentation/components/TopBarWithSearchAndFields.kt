package bob.colbaskin.umirhack7.maplibre.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bob.colbaskin.umirhack7.common.UiState
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.presentation.MapLibreAction
import bob.colbaskin.umirhack7.maplibre.presentation.MapLibreState
import compose.icons.TablerIcons
import compose.icons.tablericons.Eye
import compose.icons.tablericons.EyeOff
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithSearchAndFields(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit
) {
    var showSearchResults by remember { mutableStateOf(false) }
    var fieldsDropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            SearchTextField(
                value = state.searchQuery,
                onValueChange = { query ->
                    onAction(MapLibreAction.UpdateSearchQuery(query))
                    showSearchResults = query.isNotBlank()
                },
                onSearchPerform = {
                    onAction(MapLibreAction.PerformSearch)
                    showSearchResults = false
                },
                onClearSearch = {
                    onAction(MapLibreAction.ClearSearch)
                    showSearchResults = false
                }
            )

            SearchResultsDropdownMenu(
                expanded = showSearchResults,
                onDismissRequest = { showSearchResults = false },
                state = state,
                onFieldSelected = { field ->
                    onAction(MapLibreAction.SelectField(field))
                    showSearchResults = false
                }
            )
        }

        FieldsDropdownButton(
            state = state,
            onAction = onAction,
            expanded = fieldsDropdownExpanded,
            onExpandedChange = { fieldsDropdownExpanded = it }
        )
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchPerform: () -> Unit,
    onClearSearch: () -> Unit,
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isUserTyping by remember { mutableStateOf(false) }

    LaunchedEffect(textFieldValue.text) {
        if (isUserTyping) {
            delay(350)
            onValueChange(textFieldValue.text)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFCBCBCB), RoundedCornerShape(8.dp))
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = Color(0xFF878787),
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onSearchPerform()
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        isUserTyping = true
                        textFieldValue = newValue
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            isUserTyping = false
                            onSearchPerform()
                        }
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color(0xFF101828),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    decorationBox = { innerTextField ->
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                "Поиск полей...",
                                color = Color(0xFF878787),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                )
            }

            if (textFieldValue.text.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Очистить поиск",
                    tint = Color(0xFF878787),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            isUserTyping = false
                            textFieldValue = TextFieldValue("")
                            onClearSearch()
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                )
            }
        }
    }
}

@Composable
private fun FieldsDropdownButton(
    state: MapLibreState,
    onAction: (MapLibreAction) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        OutlinedButton(
            onClick = { onExpandedChange(true) },
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            val fieldsCount = (state.fieldsState as? UiState.Success)?.data?.size ?: 0
            Text(
                text = state.selectedField?.name ?: "Поля ($fieldsCount)",
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
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            DropdownMenuItem(
                text = { Text("Все поля") },
                onClick = {
                    onAction(MapLibreAction.ClearSelectedField)
                    onExpandedChange(false)
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = if (state.showFields) TablerIcons.Eye else TablerIcons.EyeOff,
                            contentDescription = if (state.showFields) "Скрыть поля" else "Показать поля",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onAction(MapLibreAction.ToggleFieldsVisibility)
                                }
                        )

                        when (state.fieldsState) {
                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            is UiState.Error -> {
                                Icon(
                                    imageVector = Icons.Filled.Replay,
                                    contentDescription = "Обновить поля",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            onAction(MapLibreAction.LoadFields)
                                        }
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Filled.Replay,
                                    contentDescription = "Обновить поля",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            onAction(MapLibreAction.LoadFields)
                                        }
                                )
                            }
                        }
                    }
                }
            )

            HorizontalDivider()

            when (val fieldsState = state.fieldsState) {
                is UiState.Success -> {
                    if (fieldsState.data.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Нет полей", color = Color.Gray) },
                            onClick = {}
                        )
                    } else {
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
                                    onExpandedChange(false)
                                }
                            )
                        }
                    }
                }
                is UiState.Loading -> {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                Text("Загрузка полей...")
                            }
                        },
                        onClick = {}
                    )
                }
                is UiState.Error -> {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                                Text("Ошибка загрузки", color = Color.Red)
                            }
                        },
                        onClick = {
                            onAction(MapLibreAction.LoadFields)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    state: MapLibreState,
    onFieldSelected: (Field) -> Unit
) {
    val searchResults = state.filteredFields

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        DropdownMenuItem(
            text = {
                Text("Результаты поиска (${searchResults.size})")
            },
            onClick = {}
        )

        HorizontalDivider()

        if (searchResults.isEmpty()) {
            DropdownMenuItem(
                text = {
                    Text("Поля не найдены", color = Color.Gray)
                },
                onClick = {}
            )
        } else {
            searchResults.forEach { field ->
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
                    onClick = { onFieldSelected(field) }
                )
            }
        }
    }
}
