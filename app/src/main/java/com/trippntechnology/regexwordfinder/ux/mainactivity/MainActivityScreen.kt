package com.trippntechnology.regexwordfinder.ux.mainactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trippntechnology.regexwordfinder.ui.icons.Die
import com.trippntechnology.regexwordfinder.ui.theme.AppTheme
import com.trippntechnology.regexwordfinder.ui.widget.FilterTextField
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun MainActivityScreen(viewModel: MainActivityViewModel = hiltViewModel()) {
    MainActivityContent(viewModel.uiState)
}

@Composable
private fun MainActivityContent(uiState: MainActivityUiState) {
    val queries by uiState.queryListFlow.collectAsStateWithLifecycle()
    val results by uiState.resultsFlow.collectAsStateWithLifecycle()
    val focusRequester = FocusRequester()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                SmallFloatingActionButton(onClick = uiState.onChooseRandomWord) { Icon(imageVector = Icons.Outlined.Die, contentDescription = "Sort") }
                if (results.size < 10000) SmallFloatingActionButton(onClick = uiState.onOrderByMostLikely) { Icon(imageVector = Icons.Default.SortByAlpha, contentDescription = "Sort") }
                FloatingActionButton(onClick = uiState.onAddQuery) { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add") }
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
                            .padding(bottom = 16.dp)
                            .then(if (index == queries.size - 1) Modifier.focusRequester(focusRequester) else Modifier),
                        query = query,
                        placeholder = "Regex",
                        onQueryChange = { uiState.onQueryChange(index, it) },
                        onSearch = uiState.onSearch,
                        onRemove = if (index > 0) {
                            { uiState.onRemoveQuery(index) }
                        } else null,
                        onClear = { uiState.onClearQuery(index) },
                        enabled = index == queries.size - 1,
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp), text = "Count: ${results.size}", textAlign = TextAlign.End, style = MaterialTheme.typography.bodySmall
                )
            }
            LazyColumn {
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