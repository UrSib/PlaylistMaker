package com.practicum.playlistmaker.player.domain.api

interface MediaPlayerInteractor {

    fun preparePlayer(url: String)
    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun resetPlayer()

    fun playbackControl()

    fun setListener(listener: PlayerInteractorListener)

    fun updateProgress(): Runnable

}