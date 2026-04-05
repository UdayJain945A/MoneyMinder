package com.example.moneyminder.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyminder.data.local.preferences.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = preferenceManager.themeMode
        .map { themeMode ->
            SettingsUiState(
                isDarkMode = themeMode,
                currency = ""
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    val notificationsEnabled: StateFlow<Boolean> = preferenceManager.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val currency: StateFlow<String> = preferenceManager.currency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "USD"
        )

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            preferenceManager.setThemeMode(isDark)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setNotificationsEnabled(enabled)
        }
    }

    fun setCurrency(currencyCode: String) {
        viewModelScope.launch {
            preferenceManager.setCurrency(currencyCode)
        }
    }
}

data class SettingsUiState(
    val isDarkMode: Boolean? = null,
    val currency: String = "USD"
)
