package com.tapin.student.ui.home

import android.content.Intent
import android.graphics.drawable.Animatable
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tapin.student.R
import com.tapin.student.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        setupToolbar()
        setupClickListeners()
        observeUiState()
        startNfcAnimation()
    }

    override fun onResume() {
        super.onResume()
        // Re-check NFC status each time screen comes to foreground
        viewModel.updateNfcStatus(nfcAdapter)
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_logout -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.logout()
                        findNavController().navigate(R.id.action_home_to_login)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRefreshToken.setOnClickListener {
            viewModel.refreshToken()
        }

        binding.btnMyAttendance.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_attendance)
        }

        binding.btnNfcSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    // Greeting
                    binding.tvGreeting.text = getString(R.string.home_greeting, state.userName)
                    binding.tvStudentId.text = getString(R.string.home_student_id, state.studentId.ifBlank { "N/A" })

                    // Token expiry pill
                    binding.tvTokenExpiry.text = state.tokenExpiryText
                    binding.tvTokenExpiry.isVisible = state.tokenExpiryText.isNotBlank()

                    // Refresh button loading state
                    binding.btnRefreshToken.isEnabled = !state.isRefreshingToken
                    binding.btnRefreshToken.text = if (state.isRefreshingToken) "Refreshing…" else getString(R.string.btn_refresh_token)

                    // NFC status UI
                    val nfcReady = state.nfcSupported && state.nfcEnabled && state.nfcToken != null
                    if (!state.nfcSupported) {
                        binding.tvNfcTitle.text = getString(R.string.nfc_not_supported)
                        binding.tvNfcSubtitle.text = "NFC hardware not found on this device."
                        binding.btnNfcSettings.isVisible = false
                    } else if (!state.nfcEnabled) {
                        binding.tvNfcTitle.text = getString(R.string.nfc_off_title)
                        binding.tvNfcSubtitle.text = getString(R.string.nfc_off_subtitle)
                        binding.btnNfcSettings.isVisible = true
                    } else {
                        binding.tvNfcTitle.text = getString(R.string.nfc_ready_title)
                        binding.tvNfcSubtitle.text = getString(R.string.nfc_ready_subtitle)
                        binding.btnNfcSettings.isVisible = false
                    }

                    // Toast messages
                    state.toastMessage?.let { msg ->
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                        viewModel.onToastShown()
                    }
                }
            }
        }
    }

    private fun startNfcAnimation() {
        val drawable = binding.ivNfcRing.drawable
        if (drawable is Animatable) drawable.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
