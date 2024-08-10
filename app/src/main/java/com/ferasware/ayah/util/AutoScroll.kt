package com.ferasware.ayah.util

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun AutoScroll(scrollState: ScrollState, duration: Float) {
    var userScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }.distinctUntilChanged()
            .collect { isScrolling ->
                userScrolling = isScrolling
            }
    }

    LaunchedEffect(userScrolling) {
        if (!userScrolling)
            delay(5000)
        while (true) {
            scrollState.animateScrollBy(duration)
        }
    }
}
