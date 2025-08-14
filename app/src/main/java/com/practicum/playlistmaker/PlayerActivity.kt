package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var mainThreadHandler: Handler? = null

    private lateinit var progress: TextView
    private var playerState = STATE_DEFAULT
    private lateinit var play: ImageButton
    private lateinit var pause: ImageButton
    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        mainThreadHandler = Handler(Looper.getMainLooper())
        progress = findViewById<TextView>(R.id.progress)
        play = findViewById<ImageButton>(R.id.play_button)
        pause = findViewById<ImageButton>(R.id.pause_button)
        val toolbarPlayer = findViewById<MaterialToolbar>(R.id.toolbar_player)
        toolbarPlayer.setNavigationOnClickListener { finish() }

        val trackJson = intent.getStringExtra("track_json")
        val gson = Gson()
        val track = gson.fromJson(trackJson, Track::class.java)

        val cover = findViewById<ImageView>(R.id.cover)
        Glide.with(this)
            .load(track.getCoverArtWork())
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(8))
            .into(cover)

        val trackName = findViewById<TextView>(R.id.track_name)
        trackName.text = track.trackName

        val artistName = findViewById<TextView>(R.id.artist_name)
        artistName.text = track.artistName

        val trackTimeMillis = findViewById<TextView>(R.id.track_time_content)
        trackTimeMillis.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val collectionNameGroup = findViewById<Group>(R.id.collectionNameGroup)
        val collectionName = findViewById<TextView>(R.id.collection_name_content)
        if (track.collectionName != null) {
            collectionName.text = track.collectionName
        } else {
            collectionNameGroup.visibility = View.GONE
        }

        val releaseDateGroup = findViewById<Group>(R.id.releaseDateGroup)
        val releaseDate = findViewById<TextView>(R.id.release_date_content)
        if (track.releaseDate != null) {
            val dateString = track.releaseDate
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val parsedDate = formatter.parse(dateString)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(parsedDate)
            releaseDate.text = year
        } else {
            releaseDateGroup.visibility = View.GONE
        }


        val primaryGenreName = findViewById<TextView>(R.id.primary_genre_name_content)
        primaryGenreName.text = track.primaryGenreName

        val country = findViewById<TextView>(R.id.country_content)
        country.text = track.country

        val url = track.previewUrl
        preparePlayer(url)
        play.setOnClickListener {
            mainThreadHandler?.post(updateProgress())
            playbackControl()
        }
        pause.setOnClickListener {
            mainThreadHandler?.removeCallbacks(updateProgress())
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(updateProgress())
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            pause.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(updateProgress())
            pause.visibility = View.GONE
            playerState = STATE_PREPARED
            progress.text = getString(R.string.progress)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        pause.visibility = View.VISIBLE
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        pause.visibility = View.GONE
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateProgress(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState==STATE_PLAYING){
                progress.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                }
                mainThreadHandler?.postDelayed(this, 300L)
            }
        }
    }
}