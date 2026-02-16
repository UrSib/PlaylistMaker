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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.sortedByDescending

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
        track.updateTimestamp(System.currentTimeMillis())
        appDatabase.playlistTrackDao().insertPlaylistTrack(convertFromPlaylistTrack(track))
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        val playlist = playlistDbConvertor.map(appDatabase.playlistDao().getPlaylistById(id))
        return playlist
    }

    override suspend fun getTracksInPlaylist(ids: String): Flow<List<Track>> {
        if (ids.length > 2) {
            val tracks =
                appDatabase.playlistTrackDao().getTracksFlow().map { playlistTrackEntities ->
                    convertFromPlaylistTrackEntity(playlistTrackEntities)
                }
            val idsList: List<Long> =
                ids.replace("[", "").replace("]", "").split(",").map { it.toDouble().toLong() }
            return tracks.map { trackList ->
                val filteredTracks = trackList.filter { track ->
                    idsList.contains(track.trackId)
                }
                filteredTracks.sortedByDescending { it.timestamp }
            }
        } else {
            return flowOf(emptyList())
        }
    }

    override suspend fun deleteTrackFromPlaylist(id: Long, playlist: Playlist?) {

        val gson = Gson()

        val tracksIds: MutableList<Long> =
            playlist?.playListTracksIds!!.replace("[", "").replace("]", "").split(",")
                .map { it.toDouble().toLong() } as MutableList<Long>
        tracksIds.remove(id)
        playlist!!.playListTracksIds = gson.toJson(tracksIds)
        playlist!!.playListSize -= 1
        appDatabase.playlistDao()
            .updatePlaylist(
                playlist!!.playListId,
                playlist!!.playListTracksIds,
                playlist!!.playListSize
            )
        checkTrack(id)
    }

    override suspend fun deletePlaylist(id: Long, tracks: List<Track>) {
        GlobalScope.launch {

            val tracksIds = async { tracks.map { track -> track.trackId } }
            appDatabase.playlistDao().deletePlaylist(id)
            val idsList = tracksIds.await()
            idsList.forEach { trackId ->
            }

            idsList.map { trackId ->
                async {
                    checkTrack(trackId)
                }
            }.awaitAll()
        }
    }

    override suspend fun editPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(
            playlist.playListId,
            playlist.playListName,
            playlist.playListDescription,
            playlist.playListCoverPath
        )
    }

    private suspend fun checkTrack(id: Long) {
        appDatabase.playlistDao().getPlaylistsFlow().collect { playlists ->
            val tracksIds = playlists.flatMap { playlist ->
                if (playlist.playListTracksIds.length > 2) {
                    playlist.playListTracksIds.replace("[", "").replace("]", "").split(",")
                        .map { it.toDouble().toLong() }
                } else {
                    emptyList()
                }
            }
            if (!tracksIds.contains(id)) {
                appDatabase.playlistTrackDao().deleteTrackById(id)
            }
        }
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

    private fun convertFromPlaylistTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {

        return tracks.map { track -> playlistTrackDbConvertor.map(track) }

    }

}