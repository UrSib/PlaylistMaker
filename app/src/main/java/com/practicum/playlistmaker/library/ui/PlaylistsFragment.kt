package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment: Fragment(){

    companion object{

        private const val PLAYLISTS_MESSAGE = "playlists_message"

        fun newInstance(message: String) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLISTS_MESSAGE, message)
            }
        }
    }

    private val PlaylistsViewModel: PlaylistsViewModel by viewModel {
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

        PlaylistsViewModel.observeMessage().observe(viewLifecycleOwner) {
            binding.infoText.text=it
        }
    }

}