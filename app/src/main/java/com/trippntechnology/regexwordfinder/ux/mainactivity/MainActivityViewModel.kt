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
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
) : ViewModel() {

    private val random = Random(LocalDateTime.now().nano)
    private val queriesFlow = MutableStateFlow<List<String>>(listOf<String>(""))
    private val resultsFlow = MutableStateFlow<List<String>>(emptyList())
    private val words: Map<String, Int>

    private val dialogTextFlow = MutableStateFlow<String?>(null)

    val uiState = MainActivityUiState(
        queryListFlow = queriesFlow.stateInDefault(viewModelScope, listOf("")),
        resultsFlow = resultsFlow.stateInDefault(viewModelScope, emptyList()),
        dialogTextFlow = dialogTextFlow,
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
        },
        onOrderByMostLikely = { resultsFlow.update { words -> words.sortedBy { getScrabbleScore(it) }/*.map { "$it (${getScrabbleScore(it)})" }*/ } },
        onChooseRandomWord = { dialogTextFlow.value = resultsFlow.value[random.nextInt(resultsFlow.value.size)] },
        onDismissDialog = { dialogTextFlow.value = null }
    )

    init {
        val assetFileSystem = application.assets.asFileSystem()
        val wordsPath = "words_dictionary.json".toPath()

        val contents = assetFileSystem.read(wordsPath) { readUtf8() }
        words = Json.decodeFromString<Map<String, Int>>(contents)
        resultsFlow.value = words.keys.toList()
    }

    private fun onQueryChange(index: Int, query: String) {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList[index] = query
            mutableList
        }
    }

    private fun onSearch() {
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

    private fun getScrabbleScore(word: String): Int = word.sumOf { char -> scrabbleLetterValues[char.uppercaseChar()] ?: 10 }

    companion object {
        private val scrabbleLetterValues = mapOf(
            'A' to 1, 'B' to 3, 'C' to 3, 'D' to 2, 'E' to 1, 'F' to 4, 'G' to 2, 'H' to 4, 'I' to 1, 'J' to 8, 'K' to 5, 'L' to 1, 'M' to 3,
            'N' to 1, 'O' to 1, 'P' to 3, 'Q' to 10, 'R' to 1, 'S' to 1, 'T' to 1, 'U' to 1, 'V' to 4, 'W' to 4, 'X' to 8, 'Y' to 4, 'Z' to 10
        )
    }
}