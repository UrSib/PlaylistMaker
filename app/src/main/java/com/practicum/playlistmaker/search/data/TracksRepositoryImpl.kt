package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Resource
import com.practicum.playlistmaker.search.data.dto.TracksRequest
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow{
        val response = networkClient.doRequest(TracksRequest(expression))
        when (response.resultCode) {
            -1->{
                emit(Resource.Error("Communication problem"))
            }
            200 -> {
                emit(Resource.Success((response as TracksResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }))
            }

            else -> {
                emit(Resource.Error("Communication problem"))
            }
        }
    }
}