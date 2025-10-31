package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.PlayerInteractorListener


class PlayerViewModel(private val url: String) : ViewModel(), PlayerInteractorListener {

    companion object {


        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }

    private var mainThreadHandler: Handler? = null
    private var text: String = "00:00"
    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()

    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(text)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    init {
        mainThreadHandler = Handler(Looper.getMainLooper())
        mediaPlayerInteractor.preparePlayer(url)
        mediaPlayerInteractor.setListener(this)
        Log.d("Debug", "listener init")
        playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler?.removeCallbacks(mediaPlayerInteractor.updateProgress())
        mediaPlayerInteractor.releasePlayer()
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