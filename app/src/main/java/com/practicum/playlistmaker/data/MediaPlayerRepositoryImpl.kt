package com.practicum.playlistmaker.data

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView


import androidx.core.view.isVisible
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.presentation.activitys.PlayerActivity
import java.util.Locale


class MediaPlayerRepositoryImpl() : MediaPlayerRepository {

    companion object {
         const val STATE_DEFAULT = 0
         const val STATE_PREPARED = 1
         const val STATE_PLAYING = 2
         const val STATE_PAUSED = 3
    }
    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    override fun startPlayer(pause: ImageButton) {
        mediaPlayer.start()
        pause.isVisible = true
        playerState = STATE_PLAYING
    }

    override fun pausePlayer(pause: ImageButton) {
        mediaPlayer.pause()
        pause.isVisible = false
        playerState = STATE_PAUSED

    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override  fun preparePlayer(application: Application, url: String, play: ImageButton, pause: ImageButton, progress: TextView, mainThreadHandler: Handler?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            pause.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(updateProgress(progress, mainThreadHandler))
            pause.isVisible = false
            playerState = STATE_PREPARED
            progress.text = application.getString(R.string.progress)
        }
    }

    override fun playbackControl(pause: ImageButton) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer(pause)
            }

           STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(pause)
            }
        }
    }

    override fun updateProgress(progress: TextView, mainThreadHandler: Handler?): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState== STATE_PLAYING){
                    progress.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                }
                mainThreadHandler?.postDelayed(this, 300L)
            }
        }
    }
}
