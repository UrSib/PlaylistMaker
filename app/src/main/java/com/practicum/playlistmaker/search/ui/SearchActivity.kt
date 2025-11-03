package com.practicum.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: ActivitySearchBinding
    //private var viewModel: SearchViewModel? = null
    private val tracks = mutableListOf<Track>()
    private var history = mutableListOf<Track>()
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    var searchText = ""

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private var isClickAllowed = true
    private var simpleTextWatcher: TextWatcher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)



        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.showHistory()

        historyAdapter =
            TrackAdapter(tracks = history, onTrackClick = {}, clickDebounce = ::clickDebounce)

        binding.clearHistoryButton.setOnClickListener {

            viewModel.onClearHistoryButtonClick()

            history.clear()
            binding.historyLayout.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }


        adapter = TrackAdapter(tracks = tracks, onTrackClick = { track ->

            viewModel.onTrackClick(track)

        }, clickDebounce = ::clickDebounce)


        binding.editTextSearch.setOnFocusChangeListener { view, hasFocus ->

            viewModel.showHistory()


            binding.historyLayout.isVisible =
                hasFocus && binding.editTextSearch.text.isEmpty() && history.isNotEmpty()

        }

        binding.editTextSearch.setText(searchText)

        binding.toolbarSearch.setNavigationOnClickListener {

            finish()

        }

        binding.refreshButton.setOnClickListener {

            viewModel.onRefreshButtonClick()

        }

        binding.clear.setOnClickListener {

            binding.editTextSearch.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.infoText.visibility = View.GONE
            binding.icNothingWasFound.visibility = View.GONE
            binding.icCommunicationsProblem.visibility = View.GONE
            binding.refreshButton.isVisible = false

            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)

        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clear.isVisible = clearVisibility(s)

                binding.historyLayout.isVisible =
                    binding.editTextSearch.hasFocus() && s?.isEmpty() == true && history.isNotEmpty()

                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }
        }
        simpleTextWatcher?.let { binding.editTextSearch.addTextChangedListener(it) }

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.recyclerViewHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewHistory.adapter = historyAdapter
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
        binding.progressBar.isVisible = false
        if (text.isNotEmpty()) {
            binding.infoText.isVisible = true
            when (type) {
                MessageType.NOTHING_FOUND -> binding.icNothingWasFound.isVisible = true
                MessageType.COMMUNICATION_PROBLEM -> {
                    binding.icCommunicationsProblem.isVisible = true
                    binding.refreshButton.isVisible = true
                }
            }
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.infoText.text = text
        } else {
            binding.infoText.isVisible = false
            binding.icCommunicationsProblem.isVisible = false
            binding.icNothingWasFound.isVisible = false
            binding.refreshButton.isVisible = false
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
        simpleTextWatcher?.let { binding.editTextSearch.removeTextChangedListener(it) }
    }

    private fun showLoading() {
        binding.icCommunicationsProblem.isVisible = false
        binding.icNothingWasFound.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showContent(tracksList: List<Track>) {

        binding.icCommunicationsProblem.isVisible = false
        binding.icNothingWasFound.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false

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