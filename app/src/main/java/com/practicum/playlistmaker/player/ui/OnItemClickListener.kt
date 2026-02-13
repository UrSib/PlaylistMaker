package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.library.domain.Playlist

interface OnItemClickListener {
    fun onItemClick(playlist: Playlist)
}