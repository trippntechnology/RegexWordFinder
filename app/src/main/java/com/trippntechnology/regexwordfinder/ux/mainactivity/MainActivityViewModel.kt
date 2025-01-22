package com.trippntechnology.regexwordfinder.ux.mainactivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trippntechnology.regexwordfinder.ext.stateInDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import okio.assetfilesystem.asFileSystem
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
) : ViewModel() {

    private val queriesFlow = MutableStateFlow<List<String>>(listOf<String>(""))
    private val resultsFlow = MutableStateFlow<List<String>>(emptyList())
    private val words: Map<String, Int>

    val uiState = MainActivityUiState(
        queryListFlow = queriesFlow.stateInDefault(viewModelScope, listOf("")),
        resultsFlow = resultsFlow.stateInDefault(viewModelScope, emptyList()),
        onAddQuery = {
            queriesFlow.update { list ->
                val mutableList = list.toMutableList()
                mutableList.add("")
                mutableList
            }
        },
        onQueryChange = ::onQueryChange,
        onRemoveQuery = { index ->
            queriesFlow.update { list ->
                val mutableList = list.toMutableList()
                mutableList.removeAt(index)
                mutableList
            }
        },
        onSearch = ::onSearch,
        onClearQuery = { index ->
            queriesFlow.update { list ->
                val mutableList = list.toMutableList()
                mutableList[index] = ""
                mutableList
            }
            onSearch()
        }
    )

    init {
        val assetFileSystem = application.assets.asFileSystem()
        val wordsPath = "words_dictionary.json".toPath()

        val contents = assetFileSystem.read(wordsPath) { readUtf8() }
        words = Json.decodeFromString<Map<String, Int>>(contents)
        resultsFlow.value = words.keys.toList()
    }

    fun onQueryChange(index: Int, query: String) {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList[index] = query
            mutableList
        }
    }

    fun onSearch() {
        resultsFlow.value = words.keys.toList()
        queriesFlow.value.forEach { query ->
            resultsFlow.value = if (query.isNotBlank()) {
                try {
                    val regex = Regex(pattern = query, option = RegexOption.IGNORE_CASE)
                    resultsFlow.value.mapNotNull { word -> if (regex.matches(word)) word else null }
                } catch (ex: Exception) {
                    resultsFlow.value
                }
            } else resultsFlow.value
        }
    }
}