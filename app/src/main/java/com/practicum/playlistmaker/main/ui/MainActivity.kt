package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.MediaLibraryActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

const val SHARED_PREFERENCES = "shared_preferences"
const val THEME_KEY = "theme_key"
const val HISTORY = "history"
const val TRACK_JSON_KEY = "track_json"


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchIntent = Intent(this, SearchActivity::class.java)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {

                startActivity(searchIntent)

            }

        }
        binding.search.setOnClickListener(searchClickListener)

        binding.library.setOnClickListener {

            val libraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(libraryIntent)

        }

        binding.settings.setOnClickListener {

            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)

        }

    }
}