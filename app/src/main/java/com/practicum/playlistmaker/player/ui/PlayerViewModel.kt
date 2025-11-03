package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractorListener


class PlayerViewModel(private val url: String, private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel(), PlayerInteractorListener {

    private var mainThreadHandler: Handler? = null
    private var text: String = "00:00"

    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(text)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    init {
        mainThreadHandler = Handler(Looper.getMainLooper())
        mediaPlayerInteractor.preparePlayer(url)
        mediaPlayerInteractor.setListener(this)
        playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler?.removeCallbacks(mediaPlayerInteractor.updateProgress())
        mediaPlayerInteractor.pausePlayer()
        mediaPlayerInteractor.resetPlayer()
        progressTimeLiveData.postValue("00:00")
    }

    fun onPlayClick() {
        playerStateLiveData.postValue(PlayerState.STATE_PLAYING)
        mediaPlayerInteractor.playbackControl()
        mainThreadHandler?.postDelayed(mediaPlayerInteractor.updateProgress(), 300L)
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
        mainThreadHandler?.removeCallbacks(mediaPlayerInteractor.updateProgress())
        playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
    }
    fun onDestroy(){
        onCleared()
    }
}