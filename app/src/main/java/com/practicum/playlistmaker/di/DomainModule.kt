package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.HistoryInteractor
import com.practicum.playlistmaker.search.domain.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.settings.domain.ThemeInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module


val domainModule = module {

    single<SharingInteractor> { SharingInteractorImpl(get()) }

    single<ThemeInteractor> { ThemeInteractorImpl(get()) }

    single<HistoryInteractor>{ HistoryInteractorImpl(get()) }

    single<TracksInteractor>{ TracksInteractorImpl(get()) }

    single<MediaPlayerInteractor>{ MediaPlayerInteractorImpl(get()) }

}
