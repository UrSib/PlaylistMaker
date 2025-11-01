package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.domain.ThemeInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeInteractor = Creator.provideThemeInteractor()
        binding.themeSwitcher.isChecked = themeInteractor.checkTheme()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

        }

        binding.toolbar.setNavigationOnClickListener {

            finish()

        }

        binding.agreement.setOnClickListener {

            val url = getString(R.string.offer)
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.setData(Uri.parse(url))
            startActivity(agreementIntent)

        }

        binding.sharing.setOnClickListener {

            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.setType("text/plain")
            val shareBody = getString(R.string.developer)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)))

        }

        binding.support.setOnClickListener {

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