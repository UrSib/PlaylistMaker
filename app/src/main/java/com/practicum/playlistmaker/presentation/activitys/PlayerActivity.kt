package com.practicum.playlistmaker.presentation.activitys

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerInteractorListener
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.util.dpToPx
import java.util.Locale

class PlayerActivity : AppCompatActivity(), PlayerInteractorListener {


    private var mainThreadHandler: Handler? = null
    private lateinit var progress: TextView
    private lateinit var play: ImageButton
    private lateinit var pause: ImageButton

    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
        mediaPlayerInteractor.setListener(this)


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

        val px = this.dpToPx(8F)

        Glide.with(this)
            .load(track.getCoverArtWork())
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(px))
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
            collectionNameGroup.isVisible = false
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
            releaseDateGroup.isVisible = false
        }


        val primaryGenreName = findViewById<TextView>(R.id.primary_genre_name_content)
        primaryGenreName.text = track.primaryGenreName

        val country = findViewById<TextView>(R.id.country_content)
        country.text = track.country

        val url = track.previewUrl

        mediaPlayerInteractor.preparePlayer(url)

        play.setOnClickListener {

            mainThreadHandler?.post(
                mediaPlayerInteractor.updateProgress()
            )

            mediaPlayerInteractor.playbackControl()
        }
        pause.setOnClickListener {

            mainThreadHandler?.post(
                mediaPlayerInteractor.updateProgress()
            )

            mediaPlayerInteractor.playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()

        mediaPlayerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerInteractor.releasePlayer()

        mainThreadHandler?.post(mediaPlayerInteractor.updateProgress())
    }

    override fun onProgressUpdated(progressValue: String) {
        progress.text = progressValue
    }

    override fun onPrepared() {
        play.isEnabled = true
        pause.isEnabled = true

    }

    override fun onCompletion() {
        mainThreadHandler?.removeCallbacks(mediaPlayerInteractor.updateProgress())
        pause.isVisible = false
        progress.text = getString(R.string.progress)
    }

    override fun isPlay() {
        pause.isVisible = false
    }

    override fun isPause() {
        pause.isVisible = true
    }
}