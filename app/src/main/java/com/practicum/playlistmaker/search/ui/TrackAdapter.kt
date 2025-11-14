package com.practicum.playlistmaker.search.ui


import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.TRACK_JSON_KEY
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.Track

class TrackAdapter(
    private val fragment: Fragment,
    private val tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val clickDebounce:()-> Boolean
) : RecyclerView.Adapter<TrackViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val clickTrack = tracks[position]
                onTrackClick.invoke(clickTrack)

                val gson = Gson()
                val trackJson = gson.toJson(clickTrack)
                val nextFragment = PlayerFragment()
                val bundle = Bundle()
                bundle.putString(TRACK_JSON_KEY, trackJson)
                nextFragment.arguments = bundle

                val navController = NavHostFragment.findNavController(fragment)
                navController.navigate(R.id.action_searchFragment_to_playerFragment, bundle)


            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}