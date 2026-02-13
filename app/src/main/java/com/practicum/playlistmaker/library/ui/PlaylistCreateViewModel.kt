package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

/*class PlaylistCreateViewModel(private val playlistsInteractor: PlaylistsInteractor): ViewModel() {
    fun onCreateButtonClick(playlist: Playlist){
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(playlist)
        }
    }
}*/
class PlaylistCreateViewModel(private val playlistsInteractor: PlaylistsInteractor): ViewModel() {
    suspend fun onCreateButtonClick(playlist: Playlist): Long {
        return playlistsInteractor.addPlaylist(playlist)
    }
}



