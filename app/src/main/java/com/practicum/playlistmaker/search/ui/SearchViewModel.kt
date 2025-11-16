package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.utils.debounce

class SearchViewModel(private val context: Context, private val tracksInteractor: TracksInteractor, private val historyInteractor: HistoryInteractor): ViewModel() {

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    private var latestSearchText: String = ""

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        searchRequest(changedText)
    }

    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    private fun searchRequest(text:String) {

        if (text.isNotEmpty()) {

        renderState(SearchState.Loading)

            tracksInteractor.searchTracks(
                text,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {

                            val tracks = mutableListOf<Track>()
                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)

                            }

                            if (errorMessage != null) {

                                renderState(SearchState.Error(context.getString(R.string.communications_problem),
                                    MessageType.COMMUNICATION_PROBLEM))
                            } else if (tracks.isEmpty()) {

                                renderState(SearchState.Error(context.getString(R.string.nothing_was_found),
                                    MessageType.NOTHING_FOUND))
                            } else {

                                renderState(SearchState.Content(tracks))
                            }
                        }
                    }
                })
        }

    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun onRefreshButtonClick(){
        searchRequest(latestSearchText)
    }

    fun showHistory(){
       val history = historyInteractor.showHistory()
        renderState(SearchState.History(history.toMutableList()))
    }

    fun onClearHistoryButtonClick(){
        historyInteractor.clearHistory()
        showHistory()
    }

    fun onTrackClick(track: Track){
        val history = historyInteractor.showHistory().toMutableList()
        historyInteractor.historyEditor(history, track)
        historyInteractor.saveHistory(history.toTypedArray<Track>())
    }
}