package com.trippntechnology.regexwordfinder.ux.mainactivity

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trippntechnology.regexwordfinder.ext.stateInDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import okio.assetfilesystem.asFileSystem

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
) : ViewModel() {

    private val random = Random(LocalDateTime.now().nano)
    private val queriesFlow = MutableStateFlow(listOf<TextFieldValue>(TextFieldValue("")))
    private val resultsFlow = MutableStateFlow<List<String>>(emptyList())
    private val words: Map<String, Int>

    val uiState = MainActivityUiState(
        queriesFlow = queriesFlow.stateInDefault(viewModelScope, listOf(TextFieldValue(""))),
        resultsFlow = resultsFlow.stateInDefault(viewModelScope, emptyList()),
        onAddQuery = {
            queriesFlow.update { list ->
                val mutableList = list.toMutableList()
                mutableList.add(TextFieldValue(""))
                mutableList
            }
        },
        onQueryChanged = ::onQueryChange,
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
                mutableList[index] = TextFieldValue("")
                mutableList
            }
            onSearch()
        },
        onChooseRandomWord = {
            val randomWord = resultsFlow.value[random.nextInt(resultsFlow.value.size)]
            Toast.makeText(application, randomWord, Toast.LENGTH_SHORT).show()
        },
    )

    init {
        val assetFileSystem = application.assets.asFileSystem()
        val wordsPath = "words_dictionary.json".toPath()

        val contents = assetFileSystem.read(wordsPath) { readUtf8() }
        words = Json.decodeFromString<Map<String, Int>>(contents)
        resultsFlow.value = words.filter { it.value > 0 }.keys.toList()
    }

    private fun onQueryChange(index: Int, query: TextFieldValue) {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList[index] = query
            mutableList
        }
    }

    private fun onSearch() {
        resultsFlow.value = words.keys.toList()
        queriesFlow.value.forEach { query ->
            val pattern = query.text
            resultsFlow.value = if (pattern.isNotBlank()) {
                try {
                    val regex = Regex(pattern = pattern, option = RegexOption.IGNORE_CASE)
                    resultsFlow.value.mapNotNull { word -> if (regex.matches(word)) word else null }
                } catch (ex: Exception) {
                    resultsFlow.value
                }
            } else resultsFlow.value
        }
    }
}