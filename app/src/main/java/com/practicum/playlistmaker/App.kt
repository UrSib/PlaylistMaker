package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import kotlin.apply

class App : Application() {

    var darkTheme = false


    override fun onCreate() {
        super.onCreate()
        var sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
       darkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        var sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()
    }

}