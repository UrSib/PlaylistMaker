package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {

    companion object {

        private const val PLAYLISTS_MESSAGE = "playlists_message"

        fun newInstance(message: String) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLISTS_MESSAGE, message)
            }
        }
    }

    private var playlists = mutableListOf<Playlist>()
    private lateinit var adapter: PlaylistAdapter

    private val playlistsViewModel: PlaylistsViewModel by viewModel {
        parametersOf(requireArguments().getString(PLAYLISTS_MESSAGE))
    }

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.observeMessage().observe(viewLifecycleOwner) {
            binding.infoText.text = it
        }

        playlistsViewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }

        adapter = PlaylistAdapter(this, playlists)

        binding.newPlaylistButton.setOnClickListener {
            val navController = NavHostFragment.findNavController(requireParentFragment())
            navController.navigate(com.practicum.playlistmaker.R.id.action_libraryFragment_to_playlistCreateFragment)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

    }

    fun render(state: PlaylistsState) {
        when (state) {

            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()

        }
    }

    private fun showContent(playlistsList: List<Playlist>) {
        binding.icNothingWasFound.isVisible = false
        binding.infoText.isVisible = false
        playlists.clear()
        playlists.addAll(playlistsList)
        adapter.notifyDataSetChanged()

    }

    private fun showEmpty(){
        binding.icNothingWasFound.isVisible = true
        binding.infoText.isVisible = true
        playlists.clear()
        adapter.notifyDataSetChanged()
    }

}