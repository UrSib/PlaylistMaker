package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel(private val message: String): ViewModel() {

    private val messageLiveData = MutableLiveData(message)
    fun observeMessage(): LiveData<String> = messageLiveData

}