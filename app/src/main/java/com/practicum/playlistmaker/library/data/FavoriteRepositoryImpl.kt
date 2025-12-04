package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converter.TrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

    override suspend fun addTrack(track: Track) {
        track.updateTimestamp(System.currentTimeMillis())
        appDatabase.trackDao().insertTrack(convertFromTrack(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrackEntity(convertFromTrack(track))
    }

    override fun getFavorite(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracksFlow()
            .map { trackEntities ->
                val tracks = convertFromTrackEntity(trackEntities)
                tracks.sortedByDescending { it.timestamp }
            }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertFromTrack(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }
}