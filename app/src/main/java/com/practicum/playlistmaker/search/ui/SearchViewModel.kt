package com.practicum.playlistmaker.search.ui

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor

class SearchViewModel(private val context: Context): ViewModel() {

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SearchViewModel(app)
            }
        }
    }

    private val tracksInteractor = Creator.provideTracksInteractor(context as Application)
    private val historyInteractor = Creator.provideHistoryInteractor()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    private var latestSearchText: String = ""

    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY

        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(text:String) {

        /*communicationProblem.isVisible = false
        nothingWasFound.isVisible = false
        refreshButton.isVisible = false
        progressBar.isVisible = true*/

        if (text.isNotEmpty()) {

        renderState(SearchState.Loading)

            tracksInteractor.searchTracks(
                text,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            /*progressBar.isVisible = false*/
                            val tracks = mutableListOf<Track>()
                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                                /*adapter.notifyDataSetChanged()
                                showMessage("", MessageType.NOTHING_FOUND)*/

                            }

                            if (errorMessage != null) {
                                /*showMessage(
                                    getString(R.string.communications_problem),
                                    MessageType.COMMUNICATION_PROBLEM
                                )
                                communicationProblem.isVisible = true
                                refreshButton.isVisible = true*/
                                renderState(SearchState.Error(context.getString(R.string.communications_problem),
                                    MessageType.COMMUNICATION_PROBLEM))
                            } else if (tracks.isEmpty()) {
                                /*showMessage(
                                    getString(R.string.nothing_was_found),
                                    MessageType.NOTHING_FOUND
                                )
                                nothingWasFound.isVisible = true*/
                                renderState(SearchState.Error(context.getString(R.string.nothing_was_found),
                                    MessageType.NOTHING_FOUND))
                            } else {
                                /*communicationProblem.isVisible = false
                                nothingWasFound.isVisible = false
                                refreshButton.isVisible = false*/
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
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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