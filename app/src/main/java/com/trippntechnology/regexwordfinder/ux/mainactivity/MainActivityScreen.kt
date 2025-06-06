package com.trippntechnology.regexwordfinder.ux.mainactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trippntechnology.regexwordfinder.ui.icons.Die
import com.trippntechnology.regexwordfinder.ui.theme.AppTheme
import com.trippntechnology.regexwordfinder.ui.widget.FilterTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun MainActivityScreen(viewModel: MainActivityViewModel = hiltViewModel()) {
    MainActivityContent(viewModel.uiState)
}

private const val DOES_NOT_CONTAIN_REGEX = "[^]"
private const val LOOKAHEAD_EXISTS_REGEX = "(?=.*)"
private const val LOOKAHEAD_EXCLUDES_REGEX = "(?!.*)"

private fun pasteInto(textFieldValue: TextFieldValue, clipText: String, offset: Int = 0): TextFieldValue {
    val sb = StringBuilder(textFieldValue.text)
    val position = textFieldValue.selection.min
    sb.insert(position, clipText)
    return TextFieldValue(text = sb.toString(), selection = TextRange(position + clipText.length + offset))
}

@Composable
private fun MainActivityContent(uiState: MainActivityUiState) {
    val clipboardManager = LocalClipboardManager.current
    val queries by uiState.queriesFlow.collectAsStateWithLifecycle()
    val results by uiState.resultsFlow.collectAsStateWithLifecycle()
    val isChecked by uiState.checkboxCheckedFlow.collectAsStateWithLifecycle()
    val focusRequester = FocusRequester()

    var showPasteButton by remember { mutableStateOf(clipboardManager.hasText()) }
    LaunchedEffect(Unit) {
        while (true) {
            showPasteButton = clipboardManager.hasText()
            delay(100)
        }
    }

    // FloatingActionButtonMenu state
    var fabMenuExpanded by remember { mutableStateOf(false) }
    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (fabMenuExpanded) 45f else 0f,
        label = "fabRotation"
    )

    // For scrolling to a word
    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()
    // Scroll when requested
    LaunchedEffect(uiState.scrollToWordFlow, results) {
        uiState.scrollToWordFlow.collectLatest { word ->
            if (word != null) {
                val index = results.indexOf(word)
                if (index >= 0) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index / 2)
                    }
                }
            }
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = fabMenuExpanded,
                button = {
                    FloatingActionButton(onClick = { fabMenuExpanded = !fabMenuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = if (fabMenuExpanded) "Close menu" else "Open menu",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }
            ) {
                // Regex snippet items
                FloatingActionButtonMenuItem(
                    onClick = {
                        uiState.onQueryChanged(queries.lastIndex, pasteInto(queries.last(), DOES_NOT_CONTAIN_REGEX, -1))
                        fabMenuExpanded = false
                    },
                    icon = { Text(DOES_NOT_CONTAIN_REGEX) },
                    text = { Text("Does Not Match") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        uiState.onQueryChanged(queries.lastIndex, pasteInto(queries.last(), LOOKAHEAD_EXISTS_REGEX, -1))
                        fabMenuExpanded = false
                    },
                    icon = { Text(LOOKAHEAD_EXISTS_REGEX) },
                    text = { Text("Word Contains") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        uiState.onQueryChanged(queries.lastIndex, pasteInto(queries.last(), LOOKAHEAD_EXCLUDES_REGEX, -1))
                        fabMenuExpanded = false
                    },
                    icon = { Text(LOOKAHEAD_EXCLUDES_REGEX) },
                    text = { Text("Word Excludes") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        uiState.onChooseRandomWord()
                        fabMenuExpanded = false
                    },
                    icon = { Icon(imageVector = Icons.Outlined.Die, contentDescription = "Random word") },
                    text = { Text("Random word") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        uiState.onAddQuery()
                        fabMenuExpanded = false
                    },
                    icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add query") },
                    text = { Text("Add query") }
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column {
                queries.forEachIndexed { index, query ->
                    FilterTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (index < queries.size - 1) Modifier.padding(bottom = 8.dp) else Modifier)
                            .then(if (index == queries.size - 1) Modifier.focusRequester(focusRequester) else Modifier),
                        query = query,
                        placeholder = "Regex",
                        onQueryChange = { uiState.onQueryChanged(index, it) },
                        onSearch = uiState.onSearch,
                        onRemove = if (index > 0) {
                            { uiState.onRemoveQuery(index) }
                        } else null,
                        onClear = { uiState.onClearQuery(index) },
                        enabled = index == queries.size - 1,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isChecked, onCheckedChange = uiState.onCheckboxChanged)
                    Text(modifier = Modifier.weight(1f), text = "Use popular words")
                    Text(modifier = Modifier.padding(end = 8.dp), text = "Count: ${results.size}", textAlign = TextAlign.End, style = MaterialTheme.typography.bodySmall)
                }
            }
            LazyColumn(state = listState) {
                items(results.chunked(2)) { result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        result.forEach { word ->
                            Text(
                                modifier = Modifier.weight(1f),
                                text = word,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(queries) { focusRequester.requestFocus() }
}

@PreviewLightDark
@Composable
private fun Preview() {
    val wordsList = listOf(
        "apple", "banana", "cherry", "date", "fig",
        "grape", "kiwi", "lemon", "mango", "orange",
        "peach", "pear", "plum", "quince", "raspberry",
        "strawberry", "tangerine", "watermelon", "apricot", "blueberry"
    )
    val uiState = MainActivityUiState(
        resultsFlow = MutableStateFlow(wordsList)
    )
    AppTheme { MainActivityContent(uiState) }
}