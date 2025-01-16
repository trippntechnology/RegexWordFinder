package com.trippntechnology.regexwordfinder.ux.mainactivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trippntechnology.regexwordfinder.ext.stateInDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import okio.assetfilesystem.asFileSystem
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
) : ViewModel() {

    private val queryFlow = MutableStateFlow<String>("")
    private val resultsFlow = MutableStateFlow<List<String>>(emptyList())
    private val words: Map<String, Int>
//    private var job: Job? = null

    val uiState = MainActivityUiState(
        queryFlow = queryFlow.stateInDefault(viewModelScope, ""),
        resultsFlow = resultsFlow.stateInDefault(viewModelScope, emptyList()),
        onQueryChange = { queryFlow.value = it },
        onSearch = ::onSearch
    )

    init {
        val assetFileSystem = application.assets.asFileSystem()
        val wordsPath = "words_dictionary.json".toPath()

        val contents = assetFileSystem.read(wordsPath) { readUtf8() }
        words = Json.decodeFromString<Map<String, Int>>(contents)
        resultsFlow.value = words.keys.toList()
    }

//    fun onQueryChange(query: String) {
//        queryFlow.value = query
//        job?.cancel()
//        job = viewModelScope.launch {
//            delay(1000)
//            if (job?.isCancelled == true) return@launch
//            resultsFlow.value = if (query.isNotBlank()) {
//                try {
//                    val regex = Regex(pattern = query, option = RegexOption.IGNORE_CASE)
//                    words.keys.mapNotNull { word -> if (regex.matches(word)) word else null }
//                } catch (ex: Exception) {
//                    words.keys.toList()
//                }
//            } else {
//                words.keys.toList()
//            }
//        }
//    }

    fun onSearch(query: String) {
        resultsFlow.value = if (query.isNotBlank()) {
            try {
                val regex = Regex(pattern = query, option = RegexOption.IGNORE_CASE)
                words.keys.mapNotNull { word -> if (regex.matches(word)) word else null }
            } catch (ex: Exception) {
                words.keys.toList()
            }
        } else {
            words.keys.toList()
        }
    }
}