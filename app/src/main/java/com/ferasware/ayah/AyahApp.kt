package com.ferasware.ayah

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ferasware.ayah.ui.screens.AyahAndPage
import com.ferasware.ayah.ui.screens.AyahViewModel
import com.ferasware.ayah.ui.screens.Azkar
import com.ferasware.ayah.ui.theme.kitabFont
import com.ferasware.ayah.util.UserPreferences

enum class Screen { Ayah, Page, Azkar }

@Composable
fun AyahApp(viewModel: AyahViewModel) {

    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val userPreferences by
    viewModel.userPreferencesFlow.collectAsState(initial = UserPreferences())

    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: userPreferences.startScreen.name
    )

    val quranAutoScrollState = rememberSaveable(userPreferences.autoScroll) {
        mutableStateOf(userPreferences.autoScroll)
    }

    val azkarAutoScrollState = rememberSaveable(currentScreen) {
        mutableStateOf(false)
    }

    val expanded = rememberSaveable {
        mutableStateOf(false)
    }

    val showInfoDialog = rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            AyahTopAppBar(
                expanded = expanded,
                navController = navController,
                currentScreen = currentScreen,
                viewModel = viewModel,
                showSettingsDialog = showInfoDialog,
                startScreen = userPreferences.startScreen,
                onClickNavigation = { navController.popBackStack() },
                quranAutoScroll = userPreferences.autoScroll
            ) { expanded.value = true }
        },
        bottomBar = {
            if (currentScreen != Screen.Azkar) AyahBottomNavigation(
                currentScreen = currentScreen, navController = navController
            )
        },
        floatingActionButton = {
            if (currentScreen == Screen.Azkar)
                FloatingActionButton(
                    onClick = { azkarAutoScrollState.value = !azkarAutoScrollState.value },
                    modifier = Modifier.padding(vertical = 42.dp, horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (azkarAutoScrollState.value)
                        Image(
                            painter = painterResource(R.drawable.pause_button),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    else
                        Image(
                            painter = painterResource(R.drawable.play_button),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                }
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = userPreferences.startScreen.name
        ) {
            composable(Screen.Ayah.name) {
                AyahAndPage(
                    currentScreen = currentScreen,
                    viewModel = viewModel,
                    quranAutoScrollState = quranAutoScrollState
                )
            }
            composable(Screen.Page.name) {
                AyahAndPage(
                    currentScreen = currentScreen,
                    viewModel = viewModel,
                    quranAutoScrollState = quranAutoScrollState
                )
            }
            composable(Screen.Azkar.name) {
                Azkar(autoScrollState = azkarAutoScrollState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahTopAppBar(
    modifier: Modifier = Modifier,
    expanded: MutableState<Boolean>,
    navController: NavHostController,
    currentScreen: Screen,
    startScreen: Screen,
    viewModel: AyahViewModel,
    showSettingsDialog: MutableState<Boolean>,
    onClickNavigation: () -> Unit,
    quranAutoScroll: Boolean,
    onAction: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier.padding(12.dp),
        actions = {
            if (currentScreen != Screen.Azkar) IconButton(onClick = onAction) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            StatusBarDropDownMenu(expanded, navController, showSettingsDialog)
            if (showSettingsDialog.value) {
                UserSettingsDialog(
                    showSettingsDialog = showSettingsDialog,
                    viewModel = viewModel,
                    startScreen = startScreen,
                    quranAutoScroll = quranAutoScroll
                )
                expanded.value = false
            }
        },
        navigationIcon = {
            if (currentScreen == Screen.Azkar) {
                IconButton(onClick = onClickNavigation) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        title = {
            if (currentScreen == Screen.Ayah || currentScreen == Screen.Page) Text(
                text = stringResource(R.string.top_app_bar_title),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
            else Text(
                text = stringResource(R.string.azkar),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
        }
    )
}

@Composable
fun StatusBarDropDownMenu(
    expanded: MutableState<Boolean>,
    navController: NavHostController,
    showInfoDialog: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }) {
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.azkar),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                navController.navigate(Screen.Azkar.name)
                expanded.value = false
            },
            trailingIcon = {
                Image(painterResource(R.drawable.sun_moon), null, modifier = Modifier.size(24.dp))
            })
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(id = R.string.settings),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            onClick = {
                showInfoDialog.value = true
            },
            trailingIcon = {
                Image(painterResource(R.drawable.gear), null, modifier = Modifier.size(24.dp))
            }
        )
    }
}

@Composable
fun AyahBottomNavigation(
    currentScreen: Screen,
    navController: NavHostController,
) {
    NavigationBar(
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        NavigationBarItem(
            icon = {
                Image(painterResource(id = R.drawable.ayah), null, modifier = Modifier.size(24.dp))
            },
            label = { Text(stringResource(id = R.string.ayah_nav)) }, onClick = {
                if (currentScreen == Screen.Page) {
                    navController.popBackStack()
                    navController.navigate(Screen.Ayah.name)
                }
            },
            selected = currentScreen == Screen.Ayah
        )
        NavigationBarItem(
            icon = {
                Image(painterResource(id = R.drawable.page), null, modifier = Modifier.size(24.dp))
            },
            label = {
                Text(stringResource(id = R.string.page_nav))
            },
            onClick = {
                if (currentScreen == Screen.Ayah) {
                    navController.popBackStack()
                    navController.navigate(Screen.Page.name)
                }
            },
            selected = currentScreen == Screen.Page
        )
    }
}

@Composable
fun UserSettingsDialog(
    showSettingsDialog: MutableState<Boolean>,
    viewModel: AyahViewModel,
    startScreen: Screen,
    quranAutoScroll: Boolean,
) {
    val startScreenRadioOptions = listOf(Screen.Ayah, Screen.Page)

    val autoScrollScreenRadioOptions = listOf(true, false)

    val (startScreenSelectedOption, startScreenOnOptionSelected) = rememberSaveable {
        mutableStateOf(startScreen)
    }
    val (autoScrollSelectedOption, autoScrollOnOptionSelected) = rememberSaveable {
        mutableStateOf(quranAutoScroll)
    }

    AlertDialog(
        onDismissRequest = { showSettingsDialog.value = false },
        confirmButton = {
            TextButton(onClick = {
                showSettingsDialog.value = false
            }) {
                Text(stringResource(id = R.string.close))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.settings),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                fontFamily = kitabFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Column(Modifier.selectableGroup()) {
                    Text(
                        text = stringResource(id = R.string.auto_scroll_label),
                        textAlign = TextAlign.End,
                        fontFamily = kitabFont,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = stringResource(id = R.string.auto_scroll_description),
                        textAlign = TextAlign.End,
                        fontFamily = kitabFont,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    autoScrollScreenRadioOptions.forEach { enabled ->
                        val text = when (enabled) {
                            true -> stringResource(id = R.string.enable)
                            else -> stringResource(id = R.string.disable)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (enabled == autoScrollSelectedOption),
                                    onClick = {
                                        autoScrollOnOptionSelected(enabled)
                                        viewModel.updateAutoScroll(enabled)
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = kitabFont,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            RadioButton(
                                selected = (enabled == autoScrollSelectedOption),
                                onClick = null
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Column(Modifier.selectableGroup()) {
                    Text(
                        text = stringResource(id = R.string.start_screen_label),
                        textAlign = TextAlign.End,
                        fontFamily = kitabFont,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    startScreenRadioOptions.forEach { screen ->
                        val text = when (screen) {
                            Screen.Ayah -> stringResource(id = R.string.ayah_nav)
                            else -> stringResource(id = R.string.page_nav)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (screen == startScreenSelectedOption), onClick = {
                                        startScreenOnOptionSelected(screen)
                                        viewModel.updateStartScreen(screen)
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = kitabFont,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            RadioButton(
                                selected = (screen == startScreenSelectedOption),
                                onClick = null
                            )
                        }
                    }
                }
            }
        }
    )
}