package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.Playlist

class PlaylistAdapter(private val fragment: Fragment, private val playlists: List<Playlist>): RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            val navController = NavHostFragment.findNavController(fragment)
            val bundle = Bundle().apply {
                putLong("playlist_id", playlists[position].playListId)
            }
            navController.navigate(R.id.action_libraryFragment_to_playlistFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}