package com.practicum.playlistmaker

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

const val SHARED_PREFERENCES = "shared_preferences"
const val THEME_KEY = "theme_key"
const val HISTORY = "history"


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.search)
        val library = findViewById<Button>(R.id.library)
        val settings = findViewById<Button>(R.id.settings)

        val searchIntent = Intent(this, SearchActivity::class.java)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {

                startActivity(searchIntent)

            }

        }
        search.setOnClickListener(searchClickListener)

        library.setOnClickListener {

            val libraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(libraryIntent)

        }

        settings.setOnClickListener {

            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)

        }

    }
}