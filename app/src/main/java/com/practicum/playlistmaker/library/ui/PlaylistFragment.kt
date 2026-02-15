package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.debounce
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    lateinit var newTracks: MutableList<Track>
    private var isClickAllowed = true
    private lateinit var trackClickDebounce: (Boolean) -> Unit
    private var playlistId: Long? = 0L
    private var tracks = mutableListOf<Track>()
    private lateinit var adapter: TrackAdapter

    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }
    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackClickDebounce = debounce<Boolean>(
            PlaylistFragment.Companion.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {
            isClickAllowed = true
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        playlistId = arguments?.getLong("playlist_id")

        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val menuBottomSheetContainer = binding.menuBottomSheet
        val overlay = binding.overlay
        val menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
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

        binding.menu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeDurationState().observe(viewLifecycleOwner) {
            renderDuration(it)
        }

        viewModel.observeTracksState().observe(viewLifecycleOwner) {
            newTracks = it
            renderTracks(it)
        }

        binding.delete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogButtonStyle)
                .setTitle("Удалить плейлист")
                .setMessage("Вы уверены, что хотите удалить этот плейлист?")
                .setNeutralButton("Отмена") { dialog, which -> }
                .setPositiveButton("Удалить") { dialog, which ->
                    viewModel.deletePlaylist(playlistId!!, tracks)
                    findNavController().navigateUp()
                }
                .show()
        }

        binding.menuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            share()
        }
        binding.share.setOnClickListener { share() }

        binding.edit.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("playlist_id", playlistId!!)
            }
            findNavController().navigate(
                R.id.action_playlistFragment_to_playlistEditFragment,
                bundle
            )
        }

    }


    fun render(playlist: Playlist?) {

        val filePath = playlist?.playListCoverPath
        val fileName = "cover_${playlist?.playListId}.jpg"
        val file = File(filePath, fileName)

        Glide.with(this)
            .load(file)
            .placeholder(R.drawable.cover_placeholder)
            .into(binding.cover)

        val px = requireContext().dpToPx(2F)

        Glide.with(this)
            .load(file)
            .placeholder(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(px))
            .into(binding.menuPlaylistCover)

        binding.menuPlaylistName.text = playlist?.playListName
        binding.menuSize.text =
            "${playlist?.playListSize} ${getTracksDeclension(playlist?.playListSize)}"
        binding.name.text = playlist?.playListName
        binding.description.text = playlist?.playListDescription
        binding.size.text =
            "${playlist?.playListSize} ${getTracksDeclension(playlist?.playListSize)}"

    }

    fun renderDuration(duration: Long?) {
        binding.duration.text =
            "${SimpleDateFormat("mm", Locale.getDefault()).format(duration)} минут"
    }

    fun renderTracks(newTracks: MutableList<Track>) {
        if (newTracks.size > 0) {
            binding.infoCover.isVisible = false
            binding.infoMessage.isVisible = false
            tracks = newTracks
            adapter = TrackAdapter(
                fragment = this,
                tracks = tracks,
                onTrackClick = {
                },
                clickDebounce = ::clickDebounce
            )
            binding.recyclerViewPlaylist.isVisible = true
            binding.recyclerViewPlaylist.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            binding.recyclerViewPlaylist.adapter = adapter

            adapter.notifyDataSetChanged()
        } else {
            binding.infoCover.isVisible = true
            binding.infoMessage.isVisible = true
            binding.recyclerViewPlaylist.isVisible = false
        }

    }

    fun getTracksDeclension(size: Int?): String {
        return when (size?.rem(100)) {
            in 11..19 -> "треков"
            else -> when (size?.rem(10)) {
                1 -> "трек"
                in 2..4 -> "трека"
                else -> "треков"
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        trackClickDebounce(current)
        return current
    }

    fun getTrackId(trackId: Long) {

        viewModel.deleteTrack(trackId)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun share() {
        if (newTracks.size > 0) {
            viewModel.onClickSharing()
        } else {
            Toast.makeText(
                requireContext(),
                "В данном плейлисте нет списка треков, которым можно поделиться.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}