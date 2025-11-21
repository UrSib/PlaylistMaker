package com.practicum.playlistmaker.player.domain

import kotlinx.coroutines.Job

interface MediaPlayerRepository {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()

    fun releasePlayer()

    fun resetPlayer()

    fun playbackControl()

    fun setListener(listener: PlayerInteractorListener)

    fun provideState(): PlayerState

    fun provideProgress():String

}