package com.trippntechnology.regexwordfinder.model.data

import assertk.assertions.isEqualTo
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.junit.Test

class WordsTest {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun `test word count`() {
        val fileSystem = FileSystem.SYSTEM
        val wordsPath = run {
            val userDir = System.getProperty("user.dir").orEmpty()
            val candidates = listOf(
                "$userDir/app/src/main/assets/words_dictionary.json".toPath(), // when user.dir is repo root
                "$userDir/src/main/assets/words_dictionary.json".toPath(), // when user.dir is app module
                "app/src/main/assets/words_dictionary.json".toPath(), // repo-root relative
                "src/main/assets/words_dictionary.json".toPath(), // app-module relative
            )
            candidates.firstOrNull { fileSystem.exists(it) }
                ?: error("Could not find words_dictionary.json from user.dir=$userDir")
        }

        val contents = fileSystem.source(wordsPath).buffer().use { it.readUtf8() }
        val words = json.decodeFromString<Map<String, Int>>(contents)
        assertk.assertThat(words.count()).isEqualTo(370101)
    }
}
