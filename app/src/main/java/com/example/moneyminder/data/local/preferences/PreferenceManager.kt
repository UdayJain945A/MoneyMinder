package com.example.moneyminder.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    val themeMode: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[THEME_MODE]
    }

    val currency: Flow<String> = dataStore.data.map { preferences ->
        preferences[CURRENCY_CODE] ?: "USD"
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setThemeMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = isDarkMode
        }
    }

    suspend fun setCurrency(currencyCode: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_CODE] = currencyCode
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    companion object {
        private val THEME_MODE = booleanPreferencesKey("theme_mode")
        private val CURRENCY_CODE = stringPreferencesKey("currency_code")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }
}
