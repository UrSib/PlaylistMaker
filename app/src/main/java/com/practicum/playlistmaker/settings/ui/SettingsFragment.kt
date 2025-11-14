package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkTheme()

        binding.themeSwitcher.setOnCheckedChangeListener {switcher, checked ->
            (requireActivity().applicationContext as App).switchTheme(checked)
            checkTheme()

        }

        viewModel.observeSettingsState().observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.themeSwitcher.isChecked = true
                }

                else -> {
                    binding.themeSwitcher.isChecked = false
                }
            }
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