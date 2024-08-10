package com.ferasware.ayah

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import com.ferasware.ayah.util.UserPreferencesRepository

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, USER_PREFERENCES_NAME))
    }
)

class AyahApplication : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository
    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}