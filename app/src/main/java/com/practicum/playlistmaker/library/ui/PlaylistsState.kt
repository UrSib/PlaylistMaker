package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.library.domain.Playlist

sealed interface PlaylistsState {

    data class Content(val playlists: List<Playlist>) : PlaylistsState

    object Empty: PlaylistsState



}