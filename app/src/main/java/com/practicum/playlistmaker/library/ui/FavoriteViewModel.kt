package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoriteInteractor
import kotlinx.coroutines.launch

class FavoriteViewModel(private val message: String, private val favoriteInteractor: FavoriteInteractor): ViewModel() {

    private val messageLiveData = MutableLiveData(message)
    fun observeMessage(): LiveData<String> = messageLiveData

    private val stateLiveData = MutableLiveData<FavoriteState>()

    fun observeState(): LiveData<FavoriteState> = stateLiveData

    init {
        viewModelScope.launch {
            favoriteInteractor.getFavorite().collect { favoriteList ->
                if (favoriteList.isEmpty()) {
                    stateLiveData.postValue(FavoriteState.Empty)
                } else {
                    stateLiveData.postValue(FavoriteState.Content(favoriteList))
                }
            }
        }
    }

}