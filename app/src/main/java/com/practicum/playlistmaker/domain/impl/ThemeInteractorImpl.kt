package com.practicum.playlistmaker.domain.impl


import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository): ThemeInteractor {

    override fun checkTheme(): Boolean {
        return themeRepository.checkTheme()
    }

    override fun saveTheme(darkTheme: Boolean) {
        themeRepository.saveTheme(darkTheme)
    }
}