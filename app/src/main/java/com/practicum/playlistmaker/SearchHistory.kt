package com.practicum.playlistmaker


import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import kotlin.collections.toTypedArray


class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun showHistory(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)

    }

    fun saveHistory(history: Array<Track>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit()
            ?.putString(HISTORY, json)
            ?.apply()
    }

    fun clearHistory() {
        sharedPreferences.edit()
            ?.putString(HISTORY, null)
            ?.apply()
    }

    fun historyEditor(history: MutableList<Track>, track: Track) {

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