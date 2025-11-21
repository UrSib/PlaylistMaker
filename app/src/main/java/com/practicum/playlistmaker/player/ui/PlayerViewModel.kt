package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.PlayerInteractorListener
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val url: String,
    private val mediaPlayerInteractor: MediaPlayerInteractor
) : ViewModel(), PlayerInteractorListener {

    private var text: String = "00:00"

    private var timerJob: Job? = null

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
}