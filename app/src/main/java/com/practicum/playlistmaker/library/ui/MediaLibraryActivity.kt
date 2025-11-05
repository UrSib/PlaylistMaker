package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding

private lateinit var binding: ActivityLibraryBinding

private lateinit var tabMediator: TabLayoutMediator
class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val favoriteMessage = getString(R.string.favorite_is_empty)
        val playlistsMessage = getString(R.string.playlists_is_empty)

        binding.toolbarLibrary.setNavigationOnClickListener{

            finish()
        }

        binding.viewPager.adapter = LibraryViewPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle,
            favoriteMessage = favoriteMessage,
            playlistsMessage = playlistsMessage,
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite)
                1 -> tab.text = getString(R.string.playlists)
            }
        }

        tabMediator.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}