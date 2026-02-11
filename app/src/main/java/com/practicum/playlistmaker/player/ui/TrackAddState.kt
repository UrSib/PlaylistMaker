package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track

sealed interface TrackAddState {

    data class Add(val track: Track, val playlist: Playlist): TrackAddState
    data class Contains(val track: Track, val playlist: Playlist): TrackAddState
}