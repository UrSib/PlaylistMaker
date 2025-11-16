package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.TRACK_JSON_KEY
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.utils.dpToPx
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

class PlayerFragment : Fragment() {

    private val gson: Gson by inject()
    private var url: String = ""
    private val viewModel: PlayerViewModel by viewModel { parametersOf(url) }
    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarPlayer.setNavigationOnClickListener { findNavController().navigateUp() }

        val trackJson = arguments?.getString(TRACK_JSON_KEY)
      val track = gson.fromJson(trackJson, Track::class.java)


        val px = requireContext().dpToPx(8F)

        Glide.with(this)
            .load(track.getCoverArtWork())
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(px))
            .into(binding.cover)

        binding.trackName.text = track.trackName

        binding.artistName.text = track.artistName

        binding.trackTimeContent.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.collectionName != null) {
            binding.collectionNameContent.text = track.collectionName
        } else {
            binding.collectionNameGroup.isVisible = false
        }

        if (track.releaseDate != null) {
            val dateString = track.releaseDate
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val parsedDate = formatter.parse(dateString)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(parsedDate)
            binding.releaseDateContent.text = year
        } else {
            binding.releaseDateGroup.isVisible = false
        }

        binding.primaryGenreNameContent.text = track.primaryGenreName

        binding.countryContent.text = track.country

        url = track.previewUrl

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            when (it) {
                PlayerState.STATE_PREPARED -> {
                    binding.playButton.isEnabled = true
                    binding.pauseButton.isEnabled = true
                    binding.pauseButton.isVisible = false
                    binding.progress.text = getString(R.string.progress)
                }

                PlayerState.STATE_PLAYING -> {
                    binding.pauseButton.isVisible = true
                }

                PlayerState.STATE_PAUSED -> {
                    binding.pauseButton.isVisible = false
                }

                else -> {// TODO:
                }
            }
        }

        viewModel.observeProgressTime().observe(viewLifecycleOwner) {
            binding.progress.text = it
        }

        binding.playButton.setOnClickListener {

            viewModel.onPlayClick()

        }
        binding.pauseButton.setOnClickListener {

            viewModel.onPauseClick()

        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}