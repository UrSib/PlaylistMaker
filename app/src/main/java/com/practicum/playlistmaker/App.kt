package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val SHARED_PREFERENCES = "shared_preferences"
const val THEME_KEY = "theme_key"
const val HISTORY = "history"
const val TRACK_JSON_KEY = "track_json"

class App : Application() {


    var darkTheme = false


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, viewModelModule)
        }

        darkTheme = themeInteractor.checkTheme()
        switchTheme(darkTheme)
    }
    private val themeInteractor: ThemeInteractor by inject()
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
