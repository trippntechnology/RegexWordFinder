package com.trippntechnology.regexwordfinder.ux.mainactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trippntechnology.regexwordfinder.ui.theme.AppTheme
import com.trippntechnology.regexwordfinder.ui.widget.FilterTextField
import com.trippntechnology.regexwordfinder.ui.widget.SynchronizeScrolling
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun MainActivityScreen(viewModel: MainActivityViewModel = hiltViewModel()) {
    MainActivityContent(viewModel.uiState)
}

@Composable
private fun MainActivityContent(uiState: MainActivityUiState) {
    val query by uiState.queryFlow.collectAsStateWithLifecycle()
    val results by uiState.resultsFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        SynchronizeScrolling(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            pinSyncedContent = query.isNotBlank(),
            syncedContent = { syncModifier ->
                FilterTextField(
                    modifier = syncModifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    query = query,
                    placeholder = "Regex",
                    onQueryChange = uiState.onQueryChange,
                    onSearch = uiState.onSearch,
                )
            }) { contentPadding ->
            LazyColumn(contentPadding = contentPadding) {
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