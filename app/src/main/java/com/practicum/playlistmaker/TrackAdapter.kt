package com.practicum.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            val clickTrack = tracks[position]
            onTrackClick.invoke(clickTrack)
           val playerIntent = Intent(it.context, PlayerActivity::class.java)
           val gson = Gson()
           val trackJson = gson.toJson(clickTrack)
           playerIntent.putExtra(TRACK_JSON_KEY, trackJson)
           it.context.startActivity(playerIntent)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}