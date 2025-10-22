package com.practicum.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor

class SearchActivity : AppCompatActivity() {

    private lateinit var tracksInteractor: TracksInteractor

    private lateinit var historyInteractor: HistoryInteractor

    private val tracks = mutableListOf<Track>()
    private lateinit var history: MutableList<Track>
    private lateinit var adapter: TrackAdapter


    private lateinit var historyAdapter: TrackAdapter


    var searchText = ""

    private lateinit var progressBar: ProgressBar
    private lateinit var clearHistoryButton: Button
    private lateinit var infoText: TextView
    private lateinit var nothingWasFound: ImageView
    private lateinit var communicationProblem: ImageView
    private lateinit var refreshButton: Button
    private lateinit var search: EditText
    private lateinit var historyLayout: LinearLayout

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true

    private val searchRunnable =
        Runnable { request() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        tracksInteractor = Creator.provideTracksInteractor(application)
        historyInteractor = Creator.provideHistoryInteractor()


        history = historyInteractor.showHistory().toMutableList()

        historyLayout = findViewById<LinearLayout>(R.id.history_layout)

        historyAdapter =
            TrackAdapter(tracks = history, onTrackClick = {}, clickDebounce = ::clickDebounce)

        clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            history.clear()
            historyLayout.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }


        adapter = TrackAdapter(tracks = tracks, onTrackClick = { track ->
            historyInteractor.historyEditor(history, track)
            historyInteractor.saveHistory(history.toTypedArray<Track>())
            historyAdapter.notifyDataSetChanged()
        }, clickDebounce = ::clickDebounce)


        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        search = findViewById<EditText>(R.id.edit_text_search)
        val clear = findViewById<ImageView>(R.id.clear)
        refreshButton = findViewById<Button>(R.id.refresh_button)
        infoText = findViewById<TextView>(R.id.info_text)
        communicationProblem = findViewById<ImageView>(R.id.ic_communications_problem)
        nothingWasFound = findViewById<ImageView>(R.id.ic_nothing_was_found)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        search.setOnFocusChangeListener { view, hasFocus ->
            historyLayout.isVisible =
                hasFocus && search.text.isEmpty() && history.isNotEmpty()
        }

        search.setText(searchText)

        toolbarSearch.setNavigationOnClickListener {

            finish()

        }

        refreshButton.setOnClickListener {

            request()

        }

        clear.setOnClickListener {

            search.setText("")

            tracks.clear()
            adapter.notifyDataSetChanged()
            infoText.visibility = View.GONE
            nothingWasFound.visibility = View.GONE
            communicationProblem.visibility = View.GONE
            refreshButton.isVisible = false

            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(search.windowToken, 0)

        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // TODO:
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear.isVisible = clearVisibility(s)
                historyLayout.isVisible =
                    search.hasFocus() && s?.isEmpty() == true && history.isNotEmpty()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }
        }


        search.addTextChangedListener(simpleTextWatcher)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter


        var historyRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_history)

        historyRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun clearVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("SEARCH_TEXT", searchText)
    }

    enum class MessageType {
        NOTHING_FOUND,
        COMMUNICATION_PROBLEM
    }

    private fun showMessage(text: String, type: MessageType) {
        if (text.isNotEmpty()) {
            infoText.isVisible = true
            when (type) {
                MessageType.NOTHING_FOUND -> nothingWasFound.isVisible = true
                MessageType.COMMUNICATION_PROBLEM -> {
                    communicationProblem.isVisible = true
                    refreshButton.isVisible = true
                }
            }
            tracks.clear()
            adapter.notifyDataSetChanged()
            infoText.text = text
        } else {
            infoText.isVisible = false
            communicationProblem.isVisible = false
            nothingWasFound.isVisible = false
            refreshButton.isVisible = false
        }
    }




    private fun request() {

        communicationProblem.isVisible = false
        nothingWasFound.isVisible = false
        refreshButton.isVisible = false
        progressBar.isVisible = true

        if (search.text.isNotEmpty()) {
            tracksInteractor.searchTracks(
                search.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            progressBar.isVisible = false

                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                                adapter.notifyDataSetChanged()
                                showMessage("", MessageType.NOTHING_FOUND)
                            }

                            if (errorMessage != null) {
                                showMessage(
                                    getString(R.string.communications_problem),
                                    MessageType.COMMUNICATION_PROBLEM
                                )
                                communicationProblem.isVisible = true
                                refreshButton.isVisible = true
                            } else if (tracks.isEmpty()) {
                                showMessage(
                                    getString(R.string.nothing_was_found),
                                    MessageType.NOTHING_FOUND
                                )
                                nothingWasFound.isVisible = true
                            } else {
                                communicationProblem.isVisible = false
                                nothingWasFound.isVisible = false
                                refreshButton.isVisible = false

                            }
                        }
                    }
                })
        }

    }


    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (search.text.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            progressBar.isVisible = false
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}