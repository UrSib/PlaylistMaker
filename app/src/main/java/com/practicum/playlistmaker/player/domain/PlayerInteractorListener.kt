package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractorListener {
    fun onProgressUpdated(progressValue: String)
    fun onPrepared()
    fun onCompletion()
    fun isPlay()
    fun isPause()
}