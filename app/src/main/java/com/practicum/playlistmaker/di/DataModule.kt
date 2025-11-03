package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.main.ui.SHARED_PREFERENCES
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.HistoryRepository
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.ThemeRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }

    factory{ Gson() }

    single<ThemeRepository> { ThemeRepositoryImpl(get()) }

    single { androidContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE) }

    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    single<NetworkClient> { RetrofitNetworkClient(get(),androidContext()) }

    single<TracksRepository> { TracksRepositoryImpl(get()) }

    single<MediaPlayerRepository> { MediaPlayerRepositoryImpl(get()) }

    single { MediaPlayer() }

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

}