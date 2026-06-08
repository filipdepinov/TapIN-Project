package com.tapin.student.hce

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.tapin.student.data.local.SessionDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Host Card Emulation service.
 *
 * Protocol:
 *  1. Reader (teacher phone) sends SELECT APDU with our custom AID (F0544150494E3031).
 *  2. We respond with SW_OK (0x90 0x00).
 *  3. Reader sends GET DATA APDU (0x00 0xCA 0x00 0x00).
 *  4. We respond with the AES-256 encrypted NFC token + SW_OK.
 *
 * The encrypted token is stored in DataStore after login / refresh.
 */
@AndroidEntryPoint
class TapInHceService : HostApduService() {

    companion object {
        private const val TAG = "TapInHce"

        // Our custom AID registered in hce_service.xml
        private val SELECT_AID_HEADER = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte()
        )

        // GET DATA command sent after SELECT
        private val GET_DATA_APDU = byteArrayOf(
            0x00.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x00.toByte()
        )

        // Response codes
        private val SW_OK              = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val SW_UNKNOWN_COMMAND = byteArrayOf(0x00.toByte(), 0x00.toByte())
        private val SW_NO_DATA         = byteArrayOf(0x6A.toByte(), 0x88.toByte())
    }

    @Inject
    lateinit var sessionDataStore: SessionDataStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        Log.d(TAG, "APDU received: ${commandApdu.toHex()}")

        return when {
            // SELECT AID command
            commandApdu.startsWith(SELECT_AID_HEADER) -> {
                Log.d(TAG, "SELECT AID received — responding OK")
                SW_OK
            }

            // GET DATA command — respond with the encrypted token
            commandApdu.startsWith(GET_DATA_APDU) -> {
                val token = runBlocking { sessionDataStore.nfcToken.firstOrNull() }
                if (token.isNullOrBlank()) {
                    Log.w(TAG, "No NFC token available")
                    SW_NO_DATA
                } else {
                    Log.d(TAG, "Sending NFC token (${token.length} chars)")
                    val tokenBytes = token.toByteArray(Charsets.UTF_8)
                    // Prepend length as 2 bytes (big-endian) so the reader knows how many bytes to read
                    val lenBytes = byteArrayOf(
                        ((tokenBytes.size shr 8) and 0xFF).toByte(),
                        (tokenBytes.size and 0xFF).toByte()
                    )
                    lenBytes + tokenBytes + SW_OK
                }
            }

            else -> {
                Log.w(TAG, "Unknown APDU command: ${commandApdu.toHex()}")
                SW_UNKNOWN_COMMAND
            }
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "HCE deactivated. Reason: $reason")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    // ── Helpers ──────────────────────────────────────────────

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (this.size < prefix.size) return false
        return prefix.indices.all { this[it] == prefix[it] }
    }

    private fun ByteArray.toHex(): String =
        joinToString(" ") { "%02X".format(it) }
}
