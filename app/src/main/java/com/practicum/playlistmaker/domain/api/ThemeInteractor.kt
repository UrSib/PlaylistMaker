package com.practicum.playlistmaker.domain.api

interface ThemeInteractor {

    fun checkTheme(): Boolean

    fun saveTheme(darkTheme: Boolean)
}