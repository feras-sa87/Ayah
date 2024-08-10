package com.ferasware.ayah.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferasware.ayah.R
import com.ferasware.ayah.Screen
import com.ferasware.ayah.ui.theme.kitabFont
import com.ferasware.ayah.util.AutoScroll
import kotlinx.coroutines.launch

@Composable
fun AyahAndPage(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    viewModel: AyahViewModel,
    quranAutoScrollState: MutableState<Boolean>
) {
    val scrollState = rememberScrollState(0)

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier.weight(1f),
            elevation = CardDefaults.outlinedCardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                when (viewModel.ayahUiState) {
                    AyahUiState.Loading -> {
                        LoadingScreen()
                    }

                    AyahUiState.Error -> {
                        ErrorScreen {
                            if (currentScreen == Screen.Ayah) viewModel.fetchAyah()
                            else viewModel.fetchPage(false)
                        }
                    }

                    AyahUiState.Success -> {
                        if (quranAutoScrollState.value && currentScreen == Screen.Page)
                            AutoScroll(scrollState = scrollState, duration = 5f)

                        SelectionContainer {
                            Text(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .verticalScroll(scrollState),
                                text = if (currentScreen == Screen.Ayah) viewModel.ayahState else viewModel.pageState,
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    text = if (currentScreen == Screen.Ayah) viewModel.ayahNumberSurahState
                    else "${viewModel.pageNumberState}\n${viewModel.surahsState}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End
                )
                if (currentScreen == Screen.Page) {
                    if (quranAutoScrollState.value) {
                        IconButton(
                            modifier = Modifier.padding(end = 12.dp),
                            onClick = { quranAutoScrollState.value = false })
                        {
                            Image(
                                painter = painterResource(id = R.drawable.pause_button),
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(
                            modifier = Modifier.padding(end = 12.dp),
                            onClick = { quranAutoScrollState.value = true })
                        {
                            Image(
                                painter = painterResource(id = R.drawable.play_button),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentScreen == Screen.Page) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.nextPage()
                        coroutineScope.launch { scrollState.scrollTo(0) }
                        viewModel.updateStartScreen(Screen.Page)

                    }, enabled = viewModel.intPageNumberState != 604
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = stringResource(id = R.string.next_page))
                }
                FilledTonalButton(
                    onClick = {
                        viewModel.fetchPage()
                        coroutineScope.launch {
                            scrollState.scrollTo(0)
                        }
                    }, elevation = ButtonDefaults.buttonElevation(pressedElevation = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.page_button),
                        fontSize = 20.sp,
                        fontFamily = kitabFont
                    )
                }
                OutlinedButton(
                    onClick = {
                        viewModel.previousPage()
                        coroutineScope.launch {
                            scrollState.scrollTo(0)
                        }
                    }, enabled = viewModel.intPageNumberState != 1
                ) {
                    Text(text = stringResource(id = R.string.previous_page))
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)

                }
            }
        } else {
            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                    viewModel.fetchAyah()
                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                }, elevation = ButtonDefaults.buttonElevation(
                    pressedElevation = 16.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.ayah_button),
                    fontSize = 20.sp,
                    fontFamily = kitabFont
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Image(
        modifier = Modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = null
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.error_message), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry_button))
        }
    }
}
