package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.util.Resource
import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}