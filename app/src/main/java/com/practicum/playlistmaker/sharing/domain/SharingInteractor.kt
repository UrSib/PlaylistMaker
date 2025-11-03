package com.practicum.playlistmaker.sharing.domain

interface SharingInteractor {
    fun shareApp(url: String)
    fun openTerms(url: String)
    fun openSupport(email: String)
}