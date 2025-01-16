package com.trippntechnology.regexwordfinder.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import kotlin.ranges.coerceIn

@Composable
fun SynchronizeScrolling(
    pinSyncedContent: Boolean,
    modifier: Modifier = Modifier,
    syncedContent: @Composable (Modifier) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current

    var syncedContentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val syncedContentHeightDp by remember(syncedContentSize) { mutableStateOf(with(density) { syncedContentSize.height.toDp() }) }
    var syncedContentHeightOffsetPx by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember(syncedContentSize, pinSyncedContent) {
        object : NestedScrollConnection {
            val coordinatedContentHeightPx = syncedContentSize.height.toFloat()

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (pinSyncedContent) {
                    syncedContentHeightOffsetPx = 0f
                } else {
                    val delta = available.y
                    val newOffset = syncedContentHeightOffsetPx + delta
                    syncedContentHeightOffsetPx = newOffset.coerceIn(-coordinatedContentHeightPx, 0f)
                }
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        syncedContent(Modifier
            .zIndex(1f)
            .offset { IntOffset(0, syncedContentHeightOffsetPx.roundToInt()) }
            .onSizeChanged { syncedContentSize = it }
        )
        content(PaddingValues(top = syncedContentHeightDp))
    }
}