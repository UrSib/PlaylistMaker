package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val tracks: List<Track>) : SearchState

    data class History(val history: List<Track>): SearchState

    data class Error(val message: String, val type: MessageType) : SearchState

}
enum class MessageType {
    NOTHING_FOUND,
    COMMUNICATION_PROBLEM
}