package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface FavoriteState {

    data class Content(val tracks: List<Track>) : FavoriteState

    object Empty: FavoriteState



}