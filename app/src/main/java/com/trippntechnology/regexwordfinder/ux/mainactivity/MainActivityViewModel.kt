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
    private val checkboxCheckedFlow = MutableStateFlow(false)
    private val completeWords: List<String>
    private val popularWords: List<String>

    private val resultsFlow = MutableStateFlow<List<String>>(emptyList())

    val uiState = MainActivityUiState(
        queriesFlow = queriesFlow.stateInDefault(viewModelScope, listOf(TextFieldValue(""))),
        resultsFlow = resultsFlow.stateInDefault(viewModelScope, emptyList()),
        checkboxCheckedFlow = checkboxCheckedFlow.stateInDefault(viewModelScope, false),
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
        onCheckboxChanged = {
            checkboxCheckedFlow.value = it
            onSearch()
        },
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
                } catch (ex: Exception) {
                    resultsFlow.value
                }
            } else resultsFlow.value
        }
    }
}