package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.Resource
import com.practicum.playlistmaker.search.domain.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}