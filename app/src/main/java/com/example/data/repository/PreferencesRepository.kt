package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tally_khata_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(prefs.getString("app_language", "BN") ?: "BN")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _selectedBusinessId = MutableStateFlow(prefs.getLong("selected_business_id", 1L))
    val selectedBusinessId: StateFlow<Long> = _selectedBusinessId.asStateFlow()

    private val _pinEnabled = MutableStateFlow(prefs.getBoolean("pin_enabled", false))
    val pinEnabled: StateFlow<Boolean> = _pinEnabled.asStateFlow()

    private val _pinCode = MutableStateFlow(prefs.getString("pin_code", "1234") ?: "1234")
    val pinCode: StateFlow<String> = _pinCode.asStateFlow()

    private val _isAppLocked = MutableStateFlow(prefs.getBoolean("is_app_locked", false))
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        _language.value = lang
    }

    fun setSelectedBusinessId(id: Long) {
        prefs.edit().putLong("selected_business_id", id).apply()
        _selectedBusinessId.value = id
    }

    fun setPinEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("pin_enabled", enabled).apply()
        _pinEnabled.value = enabled
        if (enabled) {
            _isAppLocked.value = true
        } else {
            _isAppLocked.value = false
        }
    }

    fun setPinCode(code: String) {
        prefs.edit().putString("pin_code", code).apply()
        _pinCode.value = code
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        if (_pinEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun setDarkMode(dark: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", dark).apply()
        _isDarkMode.value = dark
    }
}
