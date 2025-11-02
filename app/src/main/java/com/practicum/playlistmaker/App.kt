package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.ThemeInteractor

private lateinit var themeInteractor: ThemeInteractor

class App : Application() {

    var darkTheme = false


    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        themeInteractor = Creator.provideThemeInteractor()

        darkTheme = themeInteractor.checkTheme()
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
        themeInteractor.saveTheme(darkTheme)
    }

}
