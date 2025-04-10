package com.trippntechnology.regexwordfinder.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.trippntechnology.regexwordfinder.ui.theme.AppTheme

@Composable
fun FilterTextField(
    query: TextFieldValue,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onQueryChange: (TextFieldValue) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    onRemove: (() -> Unit)? = null,
) {
    TextField(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.text.isNotBlank()) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable { if (enabled) onClear() })
            } else if (onRemove != null) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.clickable { if (enabled) onRemove() })
            }
        },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        singleLine = true,
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    var query by remember { mutableStateOf("") }
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterTextField(query = TextFieldValue(query), placeholder = "Placeholder", modifier = Modifier.fillMaxWidth(), onQueryChange = { query = it.text }, onSearch = {}, onClear = {})
        }
    }
}