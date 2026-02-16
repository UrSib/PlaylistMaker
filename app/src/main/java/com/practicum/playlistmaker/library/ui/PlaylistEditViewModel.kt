package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistEditViewModel(id: Long, playlistsInteractor: PlaylistsInteractor) :
    PlaylistCreateViewModel(playlistsInteractor) {

    private var playlist: Playlist? = null
    private val playlistStateLiveData = MutableLiveData(playlist)
    fun observePlaylistState(): LiveData<Playlist?> = playlistStateLiveData

    init {
        viewModelScope.launch {
            playlist = playlistsInteractor.getPlaylist(id)
            playlistStateLiveData.postValue(playlist)
        }
    }

    suspend fun onButtonClick(playlist: Playlist) {
        playlistsInteractor.editPlaylist(playlist)
    }
}