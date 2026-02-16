package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistsInteractor {

    override suspend fun addPlaylist(playlist: Playlist):Long{
        return playlistsRepository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>>{
        return playlistsRepository.getPlaylists()
    }

    override suspend fun refreshPlaylist(track: Track, playlist: Playlist) {
        return playlistsRepository.refreshPlaylist(track, playlist)
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return playlistsRepository.getPlaylist(id)
    }

    override suspend fun getTracksInPlaylist(ids: String): Flow<List<Track>> {
        return playlistsRepository.getTracksInPlaylist(ids)
    }

    override suspend fun deleteTrackFromPlaylist(id: Long, playlist: Playlist?) {
        return playlistsRepository.deleteTrackFromPlaylist(id,playlist)
    }

    override suspend fun deletePlaylist(id: Long, tracks: List<Track>) {
        return playlistsRepository.deletePlaylist(id, tracks)
    }

    override suspend fun editPlaylist(playlist: Playlist) {
        return playlistsRepository.editPlaylist(playlist)
    }
}