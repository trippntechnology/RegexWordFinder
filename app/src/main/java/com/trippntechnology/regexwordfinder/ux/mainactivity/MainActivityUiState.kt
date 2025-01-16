package com.trippntechnology.regexwordfinder.ux.mainactivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainActivityUiState(
    val queryFlow: StateFlow<String> = MutableStateFlow(""),
    val resultsFlow: StateFlow<List<String>> = MutableStateFlow(emptyList()),
    val onQueryChange: (String) -> Unit = {},
    val onSearch: (String) -> Unit = {}
)