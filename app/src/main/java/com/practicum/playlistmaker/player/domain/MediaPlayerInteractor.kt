package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.PlayerInteractorListener
import com.practicum.playlistmaker.player.domain.PlayerState

interface MediaPlayerInteractor {

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