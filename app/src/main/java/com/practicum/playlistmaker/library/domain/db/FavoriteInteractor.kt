package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    suspend fun addTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getFavorite(): Flow<List<Track>>
}