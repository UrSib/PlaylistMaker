package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.dpToPx
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractorListener
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var viewModel: PlayerViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarPlayer.setNavigationOnClickListener { finish() }

        val trackJson = intent.getStringExtra("track_json")
        val gson = Gson()
        val track = gson.fromJson(trackJson, Track::class.java)

        val px = this.dpToPx(8F)

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

        val url = track.previewUrl

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(url))
            .get(PlayerViewModel::class.java)

        viewModel?.observePlayerState()?.observe(this) {
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

        viewModel?.observeProgressTime()?.observe(this) {
            binding.progress.text = it
        }

        binding.playButton.setOnClickListener {

            viewModel?.onPlayClick()

        }
        binding.pauseButton.setOnClickListener {

            viewModel?.onPauseClick()

        }

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel?.onDestroy()
    }

}
