package com.practicum.playlistmaker.data.sp

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.util.Creator
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.activitys.HISTORY


class HistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : HistoryRepository {


    override fun showHistory(): Array<Track> {

        val json = sharedPreferences.getString(HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)


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
