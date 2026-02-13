package com.practicum.playlistmaker.library.data.converter

import com.practicum.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.Track

class PlaylistTrackDbConvertor {

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
            track.timestamp)
    }

    fun map(track: PlaylistTrackEntity): Track {
        return Track(track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
            track.timestamp)
    }

}