package com.practicum.playlistmaker.settings.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(private val context: Context) : ViewModel() {

    val agreementUrl = context.getString(R.string.offer)

    val sharingUrl = context.getString(R.string.developer)

    val email = context.getString(R.string.email_support)
    val themeInteractor = Creator.provideThemeInteractor()
    val sharingInteractor = Creator.provideSharingInteractor()
    private val settingsStateLiveData = MutableLiveData(false)
    fun observeSettingsState(): LiveData<Boolean> = settingsStateLiveData

    fun checkTheme() {
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

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SettingsViewModel(app)
            }
        }
    }
}