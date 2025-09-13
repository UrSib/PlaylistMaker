package com.practicum.playlistmaker.domain.impl

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView
import com.practicum.playlistmaker.data.MediaPlayerRepositoryImpl.Companion.STATE_PLAYING
import com.practicum.playlistmaker.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.api.MediaPlayerRepository
import java.util.Locale

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository):
    MediaPlayerInteractor {

    override fun startPlayer(pause: ImageButton) {
        mediaPlayerRepository.startPlayer(pause)
    }

    override fun pausePlayer(pause: ImageButton) {
        mediaPlayerRepository.pausePlayer(pause)
    }
   override fun releasePlayer(){
       mediaPlayerRepository.releasePlayer()
   }

    override fun preparePlayer(
        application: Application,
        url: String,
        play: ImageButton,
        pause: ImageButton,
        progress: TextView,
        mainThreadHandler: Handler?
    ){
        mediaPlayerRepository.preparePlayer(application,url,play,pause,progress,mainThreadHandler)
    }

    override fun playbackControl(pause: ImageButton){
        mediaPlayerRepository.playbackControl(pause)
    }

    override fun updateProgress(progress: TextView, mainThreadHandler: Handler?): Runnable{
        mediaPlayerRepository.updateProgress(progress,mainThreadHandler)
        return object : Runnable {
            override fun run() {mediaPlayerRepository.updateProgress(progress,mainThreadHandler).run()
            }
        }
    }
}