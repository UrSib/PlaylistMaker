package com.practicum.playlistmaker.domain.api

import android.app.Application
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView

interface MediaPlayerInteractor {

    fun startPlayer(pause: ImageButton)

    fun pausePlayer(pause: ImageButton)

    fun releasePlayer()

    fun preparePlayer(
        application: Application,
        url: String,
        play: ImageButton,
        pause: ImageButton,
        progress: TextView,
        mainThreadHandler: Handler?
    )

    fun playbackControl(pause: ImageButton)

    fun updateProgress(progress: TextView, mainThreadHandler: Handler?): Runnable
}