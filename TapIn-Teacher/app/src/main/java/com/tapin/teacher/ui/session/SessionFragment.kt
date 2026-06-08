package com.tapin.teacher.ui.session

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tapin.teacher.R
import com.tapin.teacher.databinding.FragmentSessionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SessionFragment : Fragment() {

    private var _binding: FragmentSessionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SessionViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))

    // Our HCE AID
    private val AID = byteArrayOf(
        0xF0.toByte(), 0x54, 0x41, 0x50, 0x49, 0x4E, 0x30, 0x31
    )
    private val SELECT_APDU: ByteArray = byteArrayOf(
        0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
        AID.size.toByte()
    ) + AID
    private val GET_DATA_APDU = byteArrayOf(
        0x00.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x00.toByte()
    )
    private val SW_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())

    private lateinit var studentAdapter: StudentTapAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentSessionBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        setupPendingIntent()
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
    }

    private fun setupPendingIntent() {
        val intent = Intent(requireActivity(), requireActivity()::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun setupRecyclerView() {
        binding.rvStudents.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        studentAdapter = StudentTapAdapter()
        binding.rvStudents.adapter = studentAdapter
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.btnCloseSession.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.confirm_close_session))
                .setMessage(getString(R.string.confirm_close_message))
                .setPositiveButton(getString(R.string.btn_confirm)) { _, _ -> viewModel.closeSession() }
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Toolbar
                    binding.toolbar.title = "${state.courseName} (${state.courseCode})"

                    // Loading / ready state
                    binding.progressStarting.isVisible = state.isStartingSession
                    binding.layoutSession.isVisible   = !state.isStartingSession

                    // NFC status card
                    if (state.sessionStatus == "closed") {
                        binding.tvNfcStatus.text = getString(R.string.session_closed,
                            state.records.size)
                        binding.cardNfcStatus.setCardBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.badge_closed_bg)
                        )
                        binding.btnCloseSession.isEnabled = false
                    } else if (!state.isNfcReady) {
                        binding.tvNfcStatus.text = getString(R.string.nfc_disabled)
                    } else {
                        binding.tvNfcStatus.text = getString(R.string.nfc_scanning)
                    }

                    // Student count
                    binding.tvStudentCount.text = getString(R.string.students_present, state.records.size)

                    // Last tap feedback banner
                    state.lastTapMessage?.let { msg ->
                        val color = if (state.lastTapSuccess) R.color.success else R.color.error
                        binding.tvLastTap.text = msg
                        binding.tvLastTap.setTextColor(ContextCompat.getColor(requireContext(), color))
                        binding.tvLastTap.isVisible = true
                        vibrate(if (state.lastTapSuccess) 80L else 200L)
                        viewModel.onTapMessageShown()
                    }

                    // Student list
                    studentAdapter.submitList(state.records)

                    // Toast
                    state.toastMessage?.let { msg ->
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                        viewModel.onToastShown()
                    }

                    // Auto-navigate back when session is confirmed closed
                    if (state.sessionClosed) {
                        kotlinx.coroutines.delay(1500)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    // ── NFC foreground dispatch ───────────────────────────────

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            requireActivity(), pendingIntent, intentFilters, null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }

    /**
     * Called by MainActivity when an NFC intent arrives while this fragment is active.
     */
    fun onNfcIntent(tag: Tag?) {
        tag ?: return
        val isoDep = IsoDep.get(tag) ?: return

        try {
            isoDep.connect()
            isoDep.timeout = 5000

            // Step 1: SELECT AID
            val selectResponse = isoDep.transceive(SELECT_APDU)
            if (!selectResponse.endsWith(SW_OK)) {
                viewModel.onNfcTap("__invalid__")
                return
            }

            // Step 2: GET DATA
            val dataResponse = isoDep.transceive(GET_DATA_APDU)
            if (dataResponse.size < 4 || !dataResponse.endsWith(SW_OK)) {
                viewModel.onNfcTap("__invalid__")
                return
            }

            // Parse: first 2 bytes = token length, then token bytes, then SW_OK
            val lenHigh  = dataResponse[0].toInt() and 0xFF
            val lenLow   = dataResponse[1].toInt() and 0xFF
            val tokenLen = (lenHigh shl 8) or lenLow
            val tokenBytes = dataResponse.copyOfRange(2, 2 + tokenLen)
            val encryptedToken = String(tokenBytes, Charsets.UTF_8)

            viewModel.onNfcTap(encryptedToken)

        } catch (e: Exception) {
            viewModel.onNfcTap("__error__")
        } finally {
            try { isoDep.close() } catch (_: Exception) {}
        }
    }

    private fun ByteArray.endsWith(suffix: ByteArray): Boolean {
        if (size < suffix.size) return false
        val offset = size - suffix.size
        return suffix.indices.all { this[offset + it] == suffix[it] }
    }

    private fun vibrate(durationMs: Long) {
        val vibrator = ContextCompat.getSystemService(requireContext(), Vibrator::class.java)
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
