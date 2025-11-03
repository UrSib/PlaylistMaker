package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.Track

class HistoryInteractorImpl(private val historyRepository: HistoryRepository) : HistoryInteractor {

    override fun showHistory(): Array<Track> {
        return historyRepository.showHistory()
    }

    override fun saveHistory(history: Array<Track>) {
        historyRepository.saveHistory(history)
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }

    override fun historyEditor(history: MutableList<Track>, track: Track) {
        historyRepository.historyEditor(history, track)
    }
}