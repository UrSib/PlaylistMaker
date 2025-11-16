package com.practicum.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel()

    private val tracks = mutableListOf<Track>()
    private var history = mutableListOf<Track>()
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    var searchText = ""

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true

    private lateinit var trackClickDebounce:(Boolean)-> Unit
    private var simpleTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         trackClickDebounce = debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
            isClickAllowed = true
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.showHistory()

        historyAdapter =
            TrackAdapter(this,tracks = history, onTrackClick = { track ->
            }, clickDebounce = ::clickDebounce)

        binding.clearHistoryButton.setOnClickListener {

            viewModel.onClearHistoryButtonClick()

            history.clear()
            binding.historyLayout.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }

        adapter = TrackAdapter(fragment = this,
            tracks = tracks,
            onTrackClick = { track ->
                viewModel.onTrackClick(track)
            },
            clickDebounce = ::clickDebounce
        )



        binding.editTextSearch.setOnFocusChangeListener { view, hasFocus ->

            viewModel.showHistory()


            binding.historyLayout.isVisible =
                hasFocus && binding.editTextSearch.text.isEmpty() && history.isNotEmpty()

        }

        binding.editTextSearch.setText(searchText)

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
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
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

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.recyclerViewHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewHistory.adapter = historyAdapter
    }

    private fun clearVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
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
        trackClickDebounce(current)
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