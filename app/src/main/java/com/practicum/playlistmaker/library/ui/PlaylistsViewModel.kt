package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val message: String, private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    private val messageLiveData = MutableLiveData(message)
    fun observeMessage(): LiveData<String> = messageLiveData

    private val stateLiveData = MutableLiveData<PlaylistsState>()

    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlistsList ->
                if (playlistsList.isEmpty()) {
                    stateLiveData.postValue(PlaylistsState.Empty)
                } else {
                    stateLiveData.postValue(PlaylistsState.Content(playlistsList))
                }
            }
        }
    }
}