package com.practicum.playlistmaker.presentation.activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.presentation.activitys.App
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val agreement = findViewById<MaterialTextView>(R.id.agreement)
        val sharing = findViewById<MaterialTextView>(R.id.sharing)
        val support = findViewById<MaterialTextView>(R.id.support)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = sharedPreferences.getBoolean(THEME_KEY, false)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

        }

        toolbar.setNavigationOnClickListener {

            finish()

        }

        agreement.setOnClickListener {

            val url = getString(R.string.offer)
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.setData(Uri.parse(url))
            startActivity(agreementIntent)

        }

        sharing.setOnClickListener {

            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.setType("text/plain")
            val shareBody = getString(R.string.developer)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)))

        }

        support.setOnClickListener {

            val uri = Uri.parse("mailto:${getString(R.string.email_support)}")
                .buildUpon()
                .appendQueryParameter("subject", getString(R.string.subject_support))
                .appendQueryParameter("body", getString(R.string.body_support))
                .build()
            val supportIntent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(supportIntent)

        }
    }
}