package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

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

        val releaseDateGroup=findViewById<Group>(R.id.releaseDateGroup)
        val releaseDate = findViewById<TextView>(R.id.release_date_content)
        if (track.releaseDate != null) {
            val dateString = track.releaseDate
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val parsedDate = formatter.parse(dateString)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(parsedDate)
            releaseDate.text = year
        } else {
            releaseDateGroup.visibility= View.GONE
        }


        val primaryGenreName = findViewById<TextView>(R.id.primary_genre_name_content)
        primaryGenreName.text = track.primaryGenreName

        val country = findViewById<TextView>(R.id.country_content)
        country.text = track.country

    }

}