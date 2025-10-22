package com.practicum.playlistmaker.domain.api

interface PlayerInteractorListener {
    fun onProgressUpdated(progressValue: String)
    fun onPrepared()
    fun onCompletion()
    fun isPlay()
    fun isPause()
}