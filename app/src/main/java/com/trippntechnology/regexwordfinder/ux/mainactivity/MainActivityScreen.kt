package com.trippntechnology.regexwordfinder.ux.mainactivity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.trippntechnology.regexwordfinder.ui.theme.AppTheme

@Composable
fun MainActivityScreen() {
    MainActivityContent("Steve")
}

@Composable
private fun MainActivityContent(name: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(paddingValues)
        )

    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppTheme { MainActivityContent("Steve") }
}