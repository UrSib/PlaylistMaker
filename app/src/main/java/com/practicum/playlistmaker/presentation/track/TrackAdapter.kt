package com.practicum.playlistmaker.presentation.track

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.presentation.activitys.PlayerActivity
import com.practicum.playlistmaker.presentation.activitys.TRACK_JSON_KEY
import com.practicum.playlistmaker.domain.models.Track

class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val clickDebounce:()-> Boolean
) : RecyclerView.Adapter<TrackViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val clickTrack = tracks[position]
                onTrackClick.invoke(clickTrack)
                val playerIntent = Intent(it.context, PlayerActivity::class.java)
                val gson = Gson()
                val trackJson = gson.toJson(clickTrack)
                playerIntent.putExtra(TRACK_JSON_KEY, trackJson)
                it.context.startActivity(playerIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}