package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.sp.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.sp.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.presentation.activitys.SHARED_PREFERENCES

object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTracksRepository(application: Application): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(application))
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(provideSharedPreferences())
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(provideSharedPreferences())
    }

    fun provideTracksInteractor(application: Application): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(application))
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

}