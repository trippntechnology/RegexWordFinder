package com.trippntechnology.regexwordfinder.ux.mainactivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainActivityUiState(
    val queryListFlow: StateFlow<List<String>> = MutableStateFlow(listOf("")),
    val resultsFlow: StateFlow<List<String>> = MutableStateFlow(emptyList()),
    val dialogTextFlow: StateFlow<String?> = MutableStateFlow(null),
    val onAddQuery: () -> Unit = {},
    val onClearQuery: (Int) -> Unit = {},
    val onQueryChange: (Int, String) -> Unit = { _, _ -> },
    val onRemoveQuery: (Int) -> Unit = {},
    val onSearch: () -> Unit = {},
    val onOrderByMostLikely: () -> Unit = {},
    val onChooseRandomWord: () -> Unit = {},
    val onDismissDialog:()->Unit = {},
)