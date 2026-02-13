package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.ui.PlaylistsState
import com.practicum.playlistmaker.player.domain.PlayerInteractorListener
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val url: String,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel(), PlayerInteractorListener {

    private var text: String = "00:00"

    private var timerJob: Job? = null

    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(text)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>()

    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsStateLiveData

    private val trackAddStateLiveData = MutableLiveData<TrackAddState>()
    fun observeTrackAddState(): LiveData<TrackAddState> = trackAddStateLiveData

    init {
        mediaPlayerInteractor.preparePlayer(url)
        mediaPlayerInteractor.setListener(this)
        playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.pausePlayer()
        mediaPlayerInteractor.resetPlayer()
        progressTimeLiveData.postValue("00:00")
    }

    fun onPlayClick() {
        playerStateLiveData.postValue(PlayerState.STATE_PLAYING)
        mediaPlayerInteractor.playbackControl()
        startTimer()
    }

    fun onPauseClick() {
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
        mediaPlayerInteractor.playbackControl()
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.provideState() == PlayerState.STATE_PLAYING) {
                delay(300L)
                text = mediaPlayerInteractor.provideProgress()
                progressTimeLiveData.postValue(text)

            }
        }
    }

    override fun onCompletion() {

        playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
        progressTimeLiveData.postValue("00:00")
        timerJob?.cancel()
    }

    fun onDestroy() {
        onCleared()
    }

    fun onFavoriteClick(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite == false) {
                track.isFavorite = true
                favoriteInteractor.addTrack(track)
            } else {
                track.isFavorite = false
                favoriteInteractor.deleteTrack(track)
            }
            playerStateLiveData.postValue(mediaPlayerInteractor.provideState())
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlistsList ->
                if (playlistsList.isEmpty()) {
                    playlistsStateLiveData.postValue(PlaylistsState.Empty)
                } else {
                    playlistsStateLiveData.postValue(PlaylistsState.Content(playlistsList))
                }
            }
        }
    }

    fun processPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            if (!playlist.playListTracksIds.contains(track.trackId.toString())) {
                playlistsInteractor.refreshPlaylist(track, playlist)
                trackAddStateLiveData.postValue(TrackAddState.Add(track, playlist))
            } else {
                trackAddStateLiveData.postValue(TrackAddState.Contains(track, playlist))
            }
        }
    }
}