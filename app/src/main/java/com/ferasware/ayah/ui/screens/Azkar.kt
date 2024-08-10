package com.ferasware.ayah.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferasware.ayah.data.azkar
import com.ferasware.ayah.util.AutoScroll

@Composable
fun Azkar(autoScrollState: MutableState<Boolean>) {
    val scrollState = rememberScrollState(0)

    if (autoScrollState.value)
        AutoScroll(scrollState = scrollState, duration = 10f)

    OutlinedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(12.dp),
            text = azkar,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}