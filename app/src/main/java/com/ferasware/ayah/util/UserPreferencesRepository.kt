package com.ferasware.ayah.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ferasware.ayah.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


data class UserPreferences(
    val startScreen: Screen = Screen.Ayah,
    var autoScroll: Boolean = false
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val START_PAGE = stringPreferencesKey("start_page")
        val AUTO_SCROLL = booleanPreferencesKey("auto_scroll")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun saveStartScreen(screen: Screen) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_PAGE] = screen.name
        }
    }

    suspend fun saveAutoScroll(autoScroll: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SCROLL] = autoScroll
        }
    }


    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val startPage =
            Screen.valueOf(
                preferences[PreferencesKeys.START_PAGE] ?: Screen.Ayah.name
            )

        val autoScroll = preferences[PreferencesKeys.AUTO_SCROLL] ?: false

        return UserPreferences(startPage, autoScroll)
    }
}