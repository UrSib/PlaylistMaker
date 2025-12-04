package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.library.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository):
    FavoriteInteractor {

    override suspend fun addTrack(track: Track){
        favoriteRepository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track){
        favoriteRepository.deleteTrack(track)
    }

    override fun getFavorite(): Flow<List<Track>>{
        return favoriteRepository.getFavorite()
    }
}