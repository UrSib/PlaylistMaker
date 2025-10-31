package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractorListener

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    override fun preparePlayer(url: String) {
        mediaPlayerRepository.preparePlayer(url)
    }

    override fun startPlayer() {
        mediaPlayerRepository.startPlayer()
    }

    override fun pausePlayer() {
        mediaPlayerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        mediaPlayerRepository.releasePlayer()
    }

    override fun playbackControl() {
        mediaPlayerRepository.playbackControl()
    }

   override fun setListener(listener: PlayerInteractorListener) {
        mediaPlayerRepository.setListener(listener)
    }

    override fun updateProgress(): Runnable {
        return mediaPlayerRepository.updateProgress()

    }

}