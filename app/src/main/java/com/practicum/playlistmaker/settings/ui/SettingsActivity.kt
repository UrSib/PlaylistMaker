package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkTheme()

        binding.themeSwitcher.setOnCheckedChangeListener {switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            checkTheme()

        }

        viewModel.observeSettingsState().observe(this) {
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

            viewModel.onClickAgreement()

        }

        binding.sharing.setOnClickListener {

            viewModel.onClickSharing()

        }

        binding.support.setOnClickListener {

            viewModel.onClickSupport()

        }
    }

    private fun checkTheme() {
        viewModel.checkTheme()
    }
}