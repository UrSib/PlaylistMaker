package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteFragment: Fragment(){

    companion object{

        private const val FAVORITE_MESSAGE = "favorite_message"

        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(message: String) = FavoriteFragment().apply {
            arguments = Bundle().apply {
                putString(FAVORITE_MESSAGE, message)
            }
        }
    }

    private val tracks = mutableListOf<Track>()
    private lateinit var adapter: TrackAdapter

    private var isClickAllowed = true

    private lateinit var trackClickDebounce:(Boolean)-> Unit

    private val favoriteViewModel: FavoriteViewModel by viewModel {
        parametersOf(requireArguments().getString(FAVORITE_MESSAGE))
    }

    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackClickDebounce = debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
            isClickAllowed = true
        }

        favoriteViewModel.observeMessage().observe(viewLifecycleOwner) {
            binding.infoText.text=it
        }

        favoriteViewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }

        adapter = TrackAdapter(fragment = this,
            tracks = tracks,
            onTrackClick = { track ->

            },
            clickDebounce = ::clickDebounce
        )

        binding.recyclerViewFavorite.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFavorite.adapter = adapter
    }


    fun render(state: FavoriteState) {
        when (state) {

            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty()

        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        trackClickDebounce(current)
        return current
    }

    private fun showContent(tracksList: List<Track>) {

        binding.icNothingWasFound.isVisible = false
        binding.infoText.isVisible = false
        tracks.clear()
        tracks.addAll(tracksList)
        adapter.notifyDataSetChanged()

    }

    private fun showEmpty(){
        binding.infoText.isVisible = true
        binding.icNothingWasFound.isVisible = true
        tracks.clear()
        adapter.notifyDataSetChanged()
    }

}