package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.HISTORY
import com.practicum.playlistmaker.search.domain.HistoryRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val appDatabase: AppDatabase
) : HistoryRepository {

    override fun showHistory(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY, null) ?: return emptyArray()
        var tracks = Gson().fromJson(json, Array<Track>::class.java)


        CoroutineScope(Dispatchers.IO).launch {
            val favoriteTrackIds = appDatabase.trackDao().getTracksIds()

            tracks = tracks.map { track ->
                if (favoriteTrackIds.contains(track.trackId)) {
                    track.isFavorite = true
                } else {
                    track.isFavorite = false
                }
                track
            }.toTypedArray()


        }

        return tracks
    }

    override fun saveHistory(history: Array<Track>) {

        val json = Gson().toJson(history)
        sharedPreferences.edit()
            ?.putString(HISTORY, json)
            ?.apply()

    }

    override fun clearHistory() {
        sharedPreferences.edit()
            ?.putString(HISTORY, null)
            ?.apply()
    }

    override fun historyEditor(history: MutableList<Track>, track: Track) {

        val index = history.indexOfFirst { it.trackId == track.trackId }
        if (index != -1) {
            val track = history.removeAt(index)
            history.add(0, track)
        } else {
            history.add(0, track)
        }
        if (history.size > 10) {
            history.removeAt(history.lastIndex)
        }
    }
}