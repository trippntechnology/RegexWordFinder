package com.trippntechnology.regexwordfinder.ux.mainactivity

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainActivityUiState(
    val checkboxCheckedFlow: StateFlow<Boolean> = MutableStateFlow(false),
    val queriesFlow: StateFlow<List<TextFieldValue>> = MutableStateFlow(listOf(TextFieldValue(""))),
    val resultsFlow: StateFlow<List<String>> = MutableStateFlow(emptyList()),
    val scrollToWordFlow: StateFlow<String?> = MutableStateFlow(null),

    val onAddQuery: () -> Unit = {},
    val onCheckboxChanged: (Boolean) -> Unit = {},
    val onChooseRandomWord: () -> Unit = {},
    val onClearQuery: (Int) -> Unit = {},
    val onQueryChanged: (Int, TextFieldValue) -> Unit = { _, _ -> },
    val onRemoveQuery: (Int) -> Unit = {},
    val onSearch: () -> Unit = {},
)