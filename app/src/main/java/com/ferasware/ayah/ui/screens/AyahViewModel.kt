package com.ferasware.ayah.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ferasware.ayah.AyahApplication
import com.ferasware.ayah.R
import com.ferasware.ayah.Screen
import com.ferasware.ayah.util.UserPreferencesRepository
import com.ferasware.ayah.util.myGetAyah
import com.ferasware.ayah.util.myGetPage
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt

interface AyahUiState {
    object Error : AyahUiState
    object Loading : AyahUiState
    object Success : AyahUiState
}

class AyahViewModel(
    private val application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var ayahState by mutableStateOf("")
        private set

    var ayahNumberSurahState by mutableStateOf("")
        private set


    var pageState by mutableStateOf("")
        private set

    var pageNumberState by mutableStateOf("")
        private set

    var intPageNumberState by mutableIntStateOf(Random.nextInt(1..604))
        private set

    var surahsState by mutableStateOf("")
        private set

    var ayahUiState: AyahUiState by mutableStateOf(AyahUiState.Loading)
        private set

    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    init {
        fetchAyah()
        fetchPage()
    }

    fun updateStartScreen(screen: Screen) {
        viewModelScope.launch {
            userPreferencesRepository.saveStartScreen(screen)
        }
    }

    fun updateAutoScroll(autoScroll: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveAutoScroll(autoScroll)
        }
    }


    fun fetchAyah() {
        viewModelScope.launch {
            ayahUiState = AyahUiState.Loading
            ayahNumberSurahState = ""

            val ayah = myGetAyah()

            ayahUiState = if (ayah != null) {
                ayahState = ayah.getValue("ayah")
                ayahNumberSurahState = application.getString(
                    R.string.surah_ayah, ayah.getValue("surah"), ayah.getValue("ayahNumber")
                )
                AyahUiState.Success
            } else {
                ayahState = ""
                ayahNumberSurahState = ""

                pageNumberState = ""
                surahsState = ""
                AyahUiState.Error
            }
        }
    }

    fun nextPage() {
        intPageNumberState++
        fetchPage(true)
    }

    fun previousPage() {
        intPageNumberState--
        fetchPage(true)
    }


    fun fetchPage(isNextOrPrevious: Boolean = false) {
        viewModelScope.launch {
            ayahUiState = AyahUiState.Loading
            pageNumberState = ""
            surahsState = ""

            if (!isNextOrPrevious) intPageNumberState = Random.nextInt(1..604)


            val page = myGetPage(intPageNumberState)

            ayahUiState = if (page != null) {
                pageState = page.getValue("ayahs")
                pageNumberState =
                    application.getString(R.string.page_number, intPageNumberState.toString())
                surahsState = page.getValue("surahs")
                AyahUiState.Success
            } else {
                pageState = ""
                pageNumberState = ""
                surahsState = ""

                ayahNumberSurahState = ""
                AyahUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AyahApplication)
                AyahViewModel(application, application.userPreferencesRepository)
            }
        }
    }
}