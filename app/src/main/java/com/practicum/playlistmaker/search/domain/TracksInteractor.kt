package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}