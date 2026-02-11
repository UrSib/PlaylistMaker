package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.TRACK_JSON_KEY
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.ui.PlaylistsState
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

class PlayerFragment : Fragment() {

    private val gson: Gson by inject()

    private val themeInteractor: ThemeInteractor by inject()
    private var url: String = ""
    private val viewModel: PlayerViewModel by viewModel { parametersOf(url) }
    private lateinit var binding: FragmentPlayerBinding
    private var playlists = mutableListOf<Playlist>()
    private lateinit var adapter: PlayerAdapter

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

        val bottomSheetContainer = binding.standardBottomSheet
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.newPlaylistButton.setOnClickListener {
            val navController = NavHostFragment.findNavController(requireParentFragment())
            navController.navigate(com.practicum.playlistmaker.R.id.action_playerFragment_to_playlistCreateFragment)
        }

        binding.toolbarPlayer.setNavigationOnClickListener { findNavController().navigateUp() }

        val trackJson = arguments?.getString(TRACK_JSON_KEY)
        val track = gson.fromJson(trackJson, Track::class.java)

        colorLikeButton(track)

        adapter = PlayerAdapter(playlists, object : OnItemClickListener {
            override fun onItemClick(playlist: Playlist) {
                viewModel.processPlaylist(playlist, track)
            }
        })

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

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

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeTrackAddState().observe(viewLifecycleOwner){
            addRender(it, bottomSheetBehavior)
        }

        binding.playButton.setOnClickListener {

            viewModel.onPlayClick()

        }
        binding.pauseButton.setOnClickListener {

            viewModel.onPauseClick()

        }

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.onAddClick()
        }

        binding.likeButton.setOnClickListener {

            viewModel.onFavoriteClick(track)
            colorLikeButton(track)
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun colorLikeButton(track: Track) {
        val isDarkTheme = themeInteractor.checkTheme()
        if (track.isFavorite) {
            if (isDarkTheme) {
                binding.likeButton.setImageResource(R.drawable.ic_like_active_dark_51)
            } else {
                binding.likeButton.setImageResource(R.drawable.ic_like_active_51)
            }
        } else {
            if (isDarkTheme) {
                binding.likeButton.setImageResource(R.drawable.ic_like_dark_51)
            } else {
                binding.likeButton.setImageResource(R.drawable.ic_like_51)
            }
        }
    }

    fun render(state: PlaylistsState) {
        when (state) {

            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()

        }
    }

    private fun showContent(playlistsList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(playlistsList)
        adapter.notifyDataSetChanged()

    }

    private fun showEmpty() {
        playlists.clear()
        adapter.notifyDataSetChanged()
    }

    fun addRender(state: TrackAddState, bottomSheetBehavior: BottomSheetBehavior<LinearLayout>){
        when (state) {

            is TrackAddState.Add -> {Toast.makeText(requireContext(),"Добавлено в плейлист ${state.playlist.playListName}",Toast.LENGTH_LONG).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN}
                is TrackAddState.Contains -> Toast.makeText(requireContext(),"Трек уже добавлен плейлист ${state.playlist.playListName}",Toast.LENGTH_LONG).show()
        }
    }
}