package com.practicum.playlistmaker.player.domain

interface PlayerInteractorListener {
    fun onProgressUpdated(progressValue: String)
    fun onCompletion()

}