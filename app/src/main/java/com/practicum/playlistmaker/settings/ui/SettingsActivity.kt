package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.domain.ThemeInteractor

class SettingsActivity : AppCompatActivity() {

    private var viewModel: SettingsViewModel? = null
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkTheme()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            checkTheme()
        }

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())
            .get(SettingsViewModel::class.java)

        viewModel?.observeSettingsState()?.observe(this) {
            when (it) {
                true -> {
                    binding.themeSwitcher.isChecked = true
                }

                else -> {
                    binding.themeSwitcher.isChecked = false
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener {

            finish()

        }

        binding.agreement.setOnClickListener {

            viewModel?.onClickAgreement()

        }

        binding.sharing.setOnClickListener {

            viewModel?.onClickSharing()

        }

        binding.support.setOnClickListener {

            viewModel?.onClickSupport()

        }
    }

    private fun checkTheme() {
        viewModel?.checkTheme()
    }
}