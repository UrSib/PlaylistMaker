package com.practicum.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.utils.dpToPx
import java.io.File

class PlaylistViewHolder(private val binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistViewBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }

    fun bind(playList: Playlist){

        val px = itemView.context.dpToPx(8F)

        /*Glide.with(itemView)
            .load("/storage/emulated/0/Android/data/com.practicum.playlistmaker/files/Pictures/first_cover.jpg")//playList.playListCoverPath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(px))
            .into(binding.cover)*/

            val filePath = playList.playListCoverPath
        val fileName = "cover_${playList.playListId}.jpg"
            val file = File(filePath, fileName)

        Glide.with(itemView)
            .load(file)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(px))
            .into(binding.cover)
            //binding.cover.setImageURI(file.toUri())


        binding.name.text = playList.playListName
        //binding.size.text = playList.playListSize.toString()+" треков"
        binding.size.text = "${playList.playListSize} ${getTracksDeclension(playList.playListSize)}"

    }

    fun getTracksDeclension(size: Int): String {
        return when (size % 100) {
            in 11..19 -> "треков"
            else -> when (size % 10) {
                1 -> "трек"
                in 2..4 -> "трека"
                else -> "треков"
            }
        }
    }

}