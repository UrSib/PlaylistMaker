package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{

    viewModel{ SettingsViewModel(androidContext(), get(), get()) }

    viewModel{ SearchViewModel(androidContext(), get(),get()) }

    viewModel{(url:String) -> PlayerViewModel(url, get()) }
}