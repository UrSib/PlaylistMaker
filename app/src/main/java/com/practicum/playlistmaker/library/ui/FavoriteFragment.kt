package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteFragment: Fragment(){

    companion object{

        private const val FAVORITE_MESSAGE = "favorite_message"

        fun newInstance(message: String) = FavoriteFragment().apply {
            arguments = Bundle().apply {
                putString(FAVORITE_MESSAGE, message)
            }
        }
    }

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

        favoriteViewModel.observeMessage().observe(viewLifecycleOwner) {
            binding.infoText.text=it
        }
    }

}