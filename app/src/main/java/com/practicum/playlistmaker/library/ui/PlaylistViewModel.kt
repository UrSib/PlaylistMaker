package com.practicum.playlistmaker.library.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf
import kotlin.collections.toMutableList

class PlaylistViewModel(
    private val id: Long,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private var playlist: Playlist? = null
    private var tracks: Flow<List<Track>>? = null

    private var duration: Long? = 0L

    private val playlistStateLiveData = MutableLiveData(playlist)
    fun observePlaylistState(): LiveData<Playlist?> = playlistStateLiveData

    private val durationStateLiveData = MutableLiveData(duration)

    fun observeDurationState(): LiveData<Long?> = durationStateLiveData

    private val tracksLiveData = MutableLiveData(mutableListOf<Track>())

    fun observeTracksState(): LiveData<MutableList<Track>> = tracksLiveData

    init {
        viewModelScope.launch {
            playlist = playlistsInteractor.getPlaylist(id)
             playlistStateLiveData.postValue(playlist)
            tracks = playlistsInteractor.getTracksInPlaylist(playlist!!.playListTracksIds)
            var totalDuration: Long = 0L
            tracks?.collect { tracks ->
                totalDuration = calculateTotalDuration(tracks)
                duration = totalDuration ?: 0L
                durationStateLiveData.postValue(duration)
                 tracksLiveData.postValue(tracks.toMutableList())
            }
        }
    }

    private suspend fun calculateTotalDuration(tracks: List<Track>): Long {
        return tracks.sumOf { it.trackTimeMillis }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlist)
            playlist = playlistsInteractor.getPlaylist(id)
            playlistStateLiveData.postValue(playlist)
            tracks = playlistsInteractor.getTracksInPlaylist(playlist!!.playListTracksIds)
            var totalDuration: Long = 0L
            tracks?.collect { tracks ->
                totalDuration = calculateTotalDuration(tracks)
                duration = totalDuration ?: 0L
                durationStateLiveData.postValue(duration)
                async { tracksLiveData.postValue(tracks.toMutableList())}
            }
        }
    }

    fun deletePlaylist(id: Long, tracks: List<Track>) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(id, tracks)
        }
    }

    fun onClickSharing() {

        val shareTracks = mutableListOf<Track>()
        viewModelScope.launch { tracks?.collect { flowTracks ->
            shareTracks.addAll(flowTracks)
        }}
        val url = createSharingMessage(playlist, shareTracks)
        sharingInteractor.shareApp(url)

    }

    fun createSharingMessage(playlist: Playlist?, tracks: List<Track>): String {
        val builder = StringBuilder()
        builder.append(playlist?.playListName).append("\n")
        playlist?.playListDescription?.let { builder.append(it).append("\n") }
        builder.append("[${playlist?.playListSize}] треков\n")
        tracks.forEachIndexed { index, track ->
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis / 1000} сек)\n")
        }

        return builder.toString()
    }

    fun refresh(){
        viewModelScope.launch { playlist = playlistsInteractor.getPlaylist(id)
        playlistStateLiveData.postValue(playlist)}
    }
}