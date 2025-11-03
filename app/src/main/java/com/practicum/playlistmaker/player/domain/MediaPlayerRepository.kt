package com.practicum.playlistmaker.player.domain.api

interface MediaPlayerRepository {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()

    fun releasePlayer()

    fun resetPlayer()

    fun playbackControl()

    fun setListener(listener: PlayerInteractorListener)

    fun updateProgress(): Runnable

}