package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun addPlaylist(playlist: Playlist):Long

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun refreshPlaylist(track: Track, playlist: Playlist)

}