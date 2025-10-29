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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor

class SearchActivity : AppCompatActivity() {

    private var viewModel: SearchViewModel? = null
    private val tracks = mutableListOf<Track>()
    private var history = mutableListOf<Track>()
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
    }
    private var isClickAllowed = true
    private var simpleTextWatcher: TextWatcher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            render(it)
        }

        viewModel?.showHistory()

        historyLayout = findViewById<LinearLayout>(R.id.history_layout)

        historyAdapter =
            TrackAdapter(tracks = history, onTrackClick = {}, clickDebounce = ::clickDebounce)

        clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {

            viewModel?.onClearHistoryButtonClick()

            history.clear()
            historyLayout.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }


        adapter = TrackAdapter(tracks = tracks, onTrackClick = { track ->

            viewModel?.onTrackClick(track)

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

            viewModel?.showHistory()


            historyLayout.isVisible =
                hasFocus && search.text.isEmpty() && history.isNotEmpty()

        }

        search.setText(searchText)

        toolbarSearch.setNavigationOnClickListener {

            finish()

        }

        refreshButton.setOnClickListener {

            viewModel?.onRefreshButtonClick()

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

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clear.isVisible = clearVisibility(s)

                historyLayout.isVisible =
                    search.hasFocus() && s?.isEmpty() == true && history.isNotEmpty()

                viewModel?.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }
        }
        simpleTextWatcher?.let { search.addTextChangedListener(it) }

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

    private fun showMessage(text: String, type: MessageType) {
        progressBar.isVisible = false
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.History -> showHistory(state.history)
            is SearchState.Error -> showMessage(state.message, state.type)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { search.removeTextChangedListener(it) }
    }

    private fun showLoading() {
        communicationProblem.isVisible = false
        nothingWasFound.isVisible = false
        refreshButton.isVisible = false
        progressBar.isVisible = true
    }

    private fun showContent(tracksList: List<Track>) {

        communicationProblem.isVisible = false
        nothingWasFound.isVisible = false
        refreshButton.isVisible = false
        progressBar.isVisible = false

        tracks.clear()
        tracks.addAll(tracksList)
        adapter.notifyDataSetChanged()

    }

    private fun showHistory(tracksList: List<Track>) {
        history.clear()
        history.addAll(tracksList)
        historyAdapter.notifyDataSetChanged()
    }
}