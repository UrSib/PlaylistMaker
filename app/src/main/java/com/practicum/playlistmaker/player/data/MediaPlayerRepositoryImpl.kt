package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractorListener
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerRepositoryImpl(val mediaPlayer: MediaPlayer) : MediaPlayerRepository {

    var playerState = PlayerState.STATE_DEFAULT
   private var listener: PlayerInteractorListener? = null
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            listener?.onCompletion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED

    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun resetPlayer(){
        mediaPlayer.reset()
    }

    override fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()

            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()

            }

            PlayerState.STATE_DEFAULT -> resetPlayer()
        }
    }

    override fun setListener(listener: PlayerInteractorListener) {
        this.listener = listener
    }

    override fun updateProgress(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    val progressText = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                   listener?.onProgressUpdated(progressText)
                }
                mainThreadHandler.postDelayed(this, 300L)
            }
        }
    }

}