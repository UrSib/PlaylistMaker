package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.utils.dpToPx
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistEditFragment() : PlaylistCreateFragment() {

    private var playlistId: Long? = 0L
    private lateinit var playlist: Playlist

    val viewModel: PlaylistEditViewModel by viewModel { parametersOf(playlistId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId =
            arguments?.getLong("playlist_id")

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            if (it != null) {
                playlist = it
            }
            render(it)
        }

        binding.createButton.setOnClickListener {
            if (uriForStorage != null) {
                saveImageToPrivateStorage(uriForStorage!!, playlist.playListId)
                playlist.playListCoverPath = (File(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "myalbum"
                )).absolutePath
            }else{
                playlist.playListCoverPath = null
            }

            playlist.playListName= binding.playlistName.editText!!.text.toString()
            playlist.playListDescription = binding.description.editText?.text.toString()

            lifecycleScope.launch { viewModel.onButtonClick(playlist) }
            findNavController().navigateUp()
        }

    }

    fun render(playlist: Playlist?) {

        binding.toolbarNewPlaylist.title = "Редактировать"
        binding.createButton.text = "Сохранить"
        val filePath = playlist?.playListCoverPath
        val fileName = "cover_${playlist?.playListId}.jpg"
        val file = File(filePath, fileName)

        val px = requireContext().dpToPx(8F)

        Glide.with(this)
            .load(file)
            .placeholder(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(px))
            .into(binding.playlistCover)

        playlist?.playListName?.let { name ->
            val editableName = Editable.Factory.getInstance().newEditable(name)
            binding.playlistName.editText?.text = editableName
        }

        playlist?.playListDescription?.let { description ->
            val editableDescription =
                Editable.Factory.getInstance().newEditable(description)
            binding.description.editText?.text = editableDescription
        }
    }

    override fun closeFragment() {
        findNavController().navigateUp()
    }

}