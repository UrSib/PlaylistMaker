package com.practicum.playlistmaker.domain.api

interface ThemeRepository {

    fun checkTheme(): Boolean

    fun saveTheme(darkTheme: Boolean)
}