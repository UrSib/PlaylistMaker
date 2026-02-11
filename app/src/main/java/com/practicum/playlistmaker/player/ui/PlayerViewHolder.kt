package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistListViewBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.utils.dpToPx
import java.io.File

class PlayerViewHolder(
    private val binding: PlaylistListViewBinding,
    private val clickListener: OnItemClickListener
): RecyclerView.ViewHolder(binding.root) {

    private var currentPlaylist: Playlist? = null

    init {
        itemView.setOnClickListener {
            currentPlaylist?.let { clickListener.onItemClick(it) }
        }
    }
    companion object {
        fun from(parent: ViewGroup,clickListener: OnItemClickListener): PlayerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistListViewBinding.inflate(inflater, parent, false)
            return PlayerViewHolder(binding, clickListener)
        }
    }

    fun bind(playList: Playlist){
        currentPlaylist = playList
        binding.playlistName.text = playList.playListName
        binding.size.text = "${playList.playListSize} ${getTracksDeclension(playList.playListSize)}"

        val px = itemView.context.dpToPx(2F)

        val filePath = playList.playListCoverPath
        val fileName = "cover_${playList.playListId}.jpg"
        val file = File(filePath, fileName)

        Glide.with(itemView)
            .load(file)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(px))
            .into(binding.playlistCover)

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