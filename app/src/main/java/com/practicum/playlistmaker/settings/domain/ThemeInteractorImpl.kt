package com.practicum.playlistmaker.settings.domain

class ThemeInteractorImpl(private val themeRepository: ThemeRepository): ThemeInteractor {

    override fun checkTheme(): Boolean {
        return themeRepository.checkTheme()
    }

    override fun saveTheme(darkTheme: Boolean) {
        themeRepository.saveTheme(darkTheme)
    }
}