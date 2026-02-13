package com.practicum.playlistmaker.library.data

import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.converter.PlaylistDbConvertor
import com.practicum.playlistmaker.library.data.converter.PlaylistTrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor
) : PlaylistsRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return appDatabase.playlistDao().insertPlaylist(convertFromPlaylist(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylistsFlow()
            .map { playlistsEntities ->
                val playlists = convertFromPlaylistEntity(playlistsEntities)
                playlists
            }
    }

    override suspend fun refreshPlaylist(track: Track, playlist: Playlist) {
        val gson = Gson()
        val tracksIds: MutableList<Long> =
            gson.fromJson(playlist.playListTracksIds, ArrayList::class.java) as MutableList<Long>
        tracksIds.add(track.trackId)
        playlist.playListTracksIds = gson.toJson(tracksIds)
        playlist.playListSize += 1
         appDatabase.playlistDao()
            .updatePlaylist(playlist.playListId, playlist.playListTracksIds, playlist.playListSize)
        appDatabase.playlistTrackDao().insertPlaylistTrack(convertFromPlaylistTrack(track))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertFromPlaylist(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistTrack(track: Track): PlaylistTrackEntity {

        return playlistTrackDbConvertor.map(track)
    }

}