package com.trippntechnology.regexwordfinder.ux.mainactivity

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trippntechnology.regexwordfinder.ext.stateInDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import okio.assetfilesystem.asFileSystem
import java.time.LocalDateTime
import kotlin.random.Random

class MainActivityViewModel(
    private val application: Application,
) : ViewModel() {

    private val random = Random(LocalDateTime.now().nano)
    private val queriesFlow = MutableStateFlow(listOf<TextFieldValue>(TextFieldValue("")))
    private val checkboxCheckedFlow = MutableStateFlow(false)
    private val completeWords: List<String>
    private val popularWords: List<String>

    private val resultsFlow = MutableStateFlow<List<String>>(emptyList())
    private val scrollToWordFlow = MutableStateFlow<String?>(null)

    val uiState = MainActivityUiState(
        checkboxCheckedFlow = checkboxCheckedFlow.stateInDefault(viewModelScope, false),
        queriesFlow = queriesFlow.stateInDefault(viewModelScope, listOf(TextFieldValue(""))),
        resultsFlow = resultsFlow.stateInDefault(viewModelScope, emptyList()),
        scrollToWordFlow = scrollToWordFlow,

        onAddQuery = ::onAddQuery,
        onCheckboxChanged = ::onCheckboxChanged,
        onChooseRandomWord = ::onChooseRandomWord,
        onClearQuery = ::onClearQuery,
        onQueryChanged = ::onQueryChange,
        onRemoveQuery = ::onRemoveQuery,
        onSearch = ::onSearch,
    )

    init {
        val assetFileSystem = application.assets.asFileSystem()
        val completeWordsPath = "words_dictionary.json".toPath()
        val popularWordsPath = "popular_dictionary.json".toPath()

        val completeWordsContents = assetFileSystem.read(completeWordsPath) { readUtf8() }
        completeWords = Json.decodeFromString<Map<String, Int>>(completeWordsContents).filter { it.value > 0 }.map { it.key }

        val popularWordsContents = assetFileSystem.read(popularWordsPath) { readUtf8() }
        popularWords = Json.decodeFromString<Map<String, Int>>(popularWordsContents).filter { it.value > 0 }.map { it.key }

        resultsFlow.value = completeWords
    }

    private fun onRemoveQuery(index: Int) {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList.removeAt(index)
            mutableList
        }
    }

    private fun onClearQuery(index: Int) {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList[index] = TextFieldValue("")
            mutableList
        }
        onSearch()
    }

    private fun onChooseRandomWord() {
        val currentResults = resultsFlow.value
        if (currentResults.isNotEmpty()) {
            val randomWord = currentResults[random.nextInt(currentResults.size)]
            scrollToWordFlow.value = randomWord
            Toast.makeText(application, randomWord, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCheckboxChanged(checked: Boolean) {
        checkboxCheckedFlow.value = checked
        onSearch()
    }

    private fun onAddQuery() {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList.add(TextFieldValue(""))
            mutableList
        }
    }

    private fun onQueryChange(index: Int, query: TextFieldValue) {
        queriesFlow.update { list ->
            val mutableList = list.toMutableList()
            mutableList[index] = query
            mutableList
        }
    }

    private fun onSearch() {
        resultsFlow.value = if (checkboxCheckedFlow.value) popularWords else completeWords
        queriesFlow.value.forEach { query ->
            val pattern = query.text
            resultsFlow.value = if (pattern.isNotBlank()) {
                try {
                    val regex = Regex(pattern = pattern, option = RegexOption.IGNORE_CASE)
                    resultsFlow.value.mapNotNull { word -> if (regex.matches(word)) word else null }
                } catch (ignore: Exception) {
                    resultsFlow.value
                }
            } else resultsFlow.value
        }
    }
}
