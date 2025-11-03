package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.ThemeRepository
import com.practicum.playlistmaker.main.ui.THEME_KEY

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {

    override fun checkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun saveTheme(darkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()
    }
}