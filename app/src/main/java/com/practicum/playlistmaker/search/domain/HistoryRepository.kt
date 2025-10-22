package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.Track

interface HistoryRepository {

    fun showHistory(): Array<Track>

    fun saveHistory(history: Array<Track>)

    fun clearHistory()

    fun historyEditor(history: MutableList<Track>, track: Track)
}