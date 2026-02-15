package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun addPlaylist(playlist: Playlist):Long

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun refreshPlaylist(track: Track, playlist: Playlist)

    suspend fun getPlaylist(id: Long): Playlist

    suspend fun getTracksInPlaylist(ids:String): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(id: Long,playlist: Playlist?)

    suspend fun deletePlaylist(id: Long, tracks: List<Track>)

    suspend fun editPlaylist(playlist: Playlist)

}