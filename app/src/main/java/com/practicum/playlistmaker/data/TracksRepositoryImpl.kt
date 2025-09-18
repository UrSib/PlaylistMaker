package com.practicum.playlistmaker.data

import androidx.core.content.ContextCompat.getString
import com.practicum.playlistmaker.domain.util.Resource
import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksRequest(expression))
        return when (response.resultCode) {
            -1->{
                Resource.Error("Communication problem")
            }
            200 -> {
                Resource.Success((response as TracksResponse).results.map {
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
                })
            }

            else -> {
                Resource.Error("Communication problem")
            }
        }
    }
}