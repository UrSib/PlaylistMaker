package com.practicum.playlistmaker.domain.api

interface MediaPlayerInteractor {

    fun preparePlayer(url: String)
    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun playbackControl()

    fun setListener(listener: PlayerInteractorListener)

    fun updateProgress(): Runnable

}