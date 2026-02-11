package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.library.domain.Playlist
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment : Fragment() {

    private val gson: Gson by inject()
    var uriForStorage: Uri? = null

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var binding: FragmentCreatePlaylistBinding
    private var simpleTextWatcher: TextWatcher? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val playlistCreateViewModel: PlaylistCreateViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Cover", "Старт экрана")

        binding.createButton.isEnabled = false

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                uriForStorage = uri

                binding.playlistCover.setImageURI(uriForStorage)

            }

        binding.toolbarNewPlaylist.setNavigationOnClickListener { closeFragment() }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                clickable()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.playlistName.editText?.doOnTextChanged { inputText, _, _, _ ->
            clickable()
        }


        clickable()

        binding.createButton.setOnClickListener {

            val name = binding.playlistName.editText?.text.toString()
            val description = binding.description.editText?.text.toString()
            val cover = (File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )).absolutePath
            val playListTracksIds = mutableListOf<Long>()
            val jsonString = gson.toJson(playListTracksIds)
            val playlist = Playlist(0, name, description, cover, jsonString, 0)

            lifecycleScope.launch {
                Log.d("Cover", "Старт лайф")
                val newPlaylistId: Long = playlistCreateViewModel.onCreateButtonClick(playlist)
                //saveImageToPrivateStorage(uriForStorage!!, newPlaylistId)
                Log.d("Cover", "Готов сравнить uri")
                if (uriForStorage != null) {
                    Log.d("Cover", "Готов сохранить изображение")
                    saveImageToPrivateStorage(uriForStorage!!, newPlaylistId)
                } else {
                    Log.d("Cover", "uriForStorage равен null")
                }
            }


            Toast.makeText(requireContext(), "Плейлист ${name} создан", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }

       confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogButtonStyle)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена"){dialog,which ->}
            .setPositiveButton("Завершить"){dialog, which ->
                findNavController().navigateUp()}

    }

    private fun clickable() {
        binding.playlistName.editText?.text.toString().let {

            if (it.isEmpty()) {
                binding.createButton.isEnabled = false
                binding.createButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    )
                )
            } else {
                binding.createButton.isEnabled = true
                binding.createButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, playlistId: Long) {
        Log.d("Cover", "Старт сохранения")

        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val fileName = "cover_${playlistId}.jpg"
        val file = File(filePath, fileName)

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        Log.d("Cover", "Файл сохранён по пути: ${file.absolutePath}")
    }

    private fun closeFragment() {

        if (binding.playlistName.editText?.text?.isNotEmpty() == true || binding.description.editText?.text?.isNotEmpty() == true) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }
}



