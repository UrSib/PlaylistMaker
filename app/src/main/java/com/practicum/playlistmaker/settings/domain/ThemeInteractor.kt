package com.practicum.playlistmaker.settings.domain

interface ThemeInteractor {

    fun checkTheme(): Boolean

    fun saveTheme(darkTheme: Boolean)
}