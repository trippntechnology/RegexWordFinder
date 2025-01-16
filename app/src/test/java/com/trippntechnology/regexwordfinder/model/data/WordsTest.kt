package com.trippntechnology.regexwordfinder.model.data

import assertk.assertions.isEqualTo
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import org.junit.Test

class WordsTest {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun `test word count`() {
        val fileSystem = FileSystem.SYSTEM
        val wordsPath = "/home/steve/AndroidStudioProjects/RegexWordFinder/app/src/main/assets/words_dictionary.json".toPath()

        val contents = fileSystem.read(wordsPath) { readUtf8() }
        val words = json.decodeFromString<Map<String, Int>>(contents)
        assertk.assertThat(words.count()).isEqualTo(370100)
    }
}