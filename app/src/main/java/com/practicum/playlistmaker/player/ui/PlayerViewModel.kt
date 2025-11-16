package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.PlayerInteractorListener
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor


class PlayerViewModel(private val url: String, private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel(), PlayerInteractorListener {

    private var text: String = "00:00"

    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(text)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

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
        mediaPlayerInteractor.updateProgress()
    }

    fun onPauseClick() {
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
        mediaPlayerInteractor.playbackControl()
    }

    override fun onProgressUpdated(progressValue: String) {
        text = progressValue
        progressTimeLiveData.postValue(text)
    }

    override fun onCompletion() {

        playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
    }
    fun onDestroy(){
        onCleared()
    }
}