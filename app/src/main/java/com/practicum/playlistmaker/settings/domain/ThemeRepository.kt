package com.practicum.playlistmaker.settings.domain

interface ThemeRepository {

    fun checkTheme(): Boolean

    fun saveTheme(darkTheme: Boolean)
}