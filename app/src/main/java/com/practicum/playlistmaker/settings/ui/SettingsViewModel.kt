package com.practicum.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(context: Context, private val sharingInteractor: SharingInteractor, private val themeInteractor: ThemeInteractor) : ViewModel() {

    val agreementUrl = context.getString(R.string.offer)

    val sharingUrl = context.getString(R.string.developer)

    val email = context.getString(R.string.email_support)


    private val settingsStateLiveData = MutableLiveData(false)
    fun observeSettingsState(): LiveData<Boolean> = settingsStateLiveData

    init {
        settingsStateLiveData.value = themeInteractor.checkTheme()
    }

    fun checkTheme() {
        themeInteractor.saveTheme(themeInteractor.checkTheme())
        settingsStateLiveData.postValue(themeInteractor.checkTheme())
    }

    fun onClickAgreement() {
        sharingInteractor.openTerms(agreementUrl)
    }

    fun onClickSupport(){
        sharingInteractor.openSupport(email)
    }

    fun onClickSharing(){
        sharingInteractor.shareApp(sharingUrl)
    }

}