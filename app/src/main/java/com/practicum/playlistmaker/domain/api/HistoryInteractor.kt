package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface HistoryInteractor {

    fun showHistory(): Array<Track>

    fun saveHistory(history: Array<Track>)

    fun clearHistory()

    fun historyEditor(history: MutableList<Track>, track: Track)

}