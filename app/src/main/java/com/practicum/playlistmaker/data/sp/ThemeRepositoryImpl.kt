package com.practicum.playlistmaker.data.sp

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.presentation.activitys.THEME_KEY

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