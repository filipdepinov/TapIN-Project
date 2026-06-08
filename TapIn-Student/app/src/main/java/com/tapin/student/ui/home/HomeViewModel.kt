package com.tapin.student.ui.home

import android.nfc.NfcAdapter
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tapin.student.data.local.SessionDataStore
import com.tapin.student.data.remote.api.ApiService
import com.tapin.student.data.remote.dto.ApiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String   = "",
    val studentId: String  = "",
    val nfcToken: String?  = null,
    val nfcEnabled: Boolean = true,
    val nfcSupported: Boolean = true,
    val tokenExpiryText: String = "",
    val isRefreshingToken: Boolean = false,
    val toastMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 5-minute countdown timer for token expiry
    private var countDownTimer: CountDownTimer? = null

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            combine(
                sessionDataStore.userName,
                sessionDataStore.studentId,
                sessionDataStore.nfcToken
            ) { name, sid, token -> Triple(name, sid, token) }
                .collect { (name, sid, token) ->
                    _uiState.value = _uiState.value.copy(
                        userName  = name ?: "",
                        studentId = sid ?: "",
                        nfcToken  = token
                    )
                    if (token != null) startExpiryCountdown()
                }
        }
    }

    fun updateNfcStatus(adapter: NfcAdapter?) {
        val supported = adapter != null
        val enabled   = adapter?.isEnabled == true
        _uiState.value = _uiState.value.copy(
            nfcSupported = supported,
            nfcEnabled   = enabled
        )
    }

    fun refreshToken() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshingToken = true)
            try {
                val response = apiService.refreshNfcToken()
                if (response.isSuccessful) {
                    val newToken = response.body()!!.nfcToken
                    sessionDataStore.saveNfcToken(newToken)
                    _uiState.value = _uiState.value.copy(
                        nfcToken = newToken,
                        isRefreshingToken = false,
                        toastMessage = "Token refreshed successfully"
                    )
                    startExpiryCountdown()
                } else {
                    val err = try {
                        Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error
                    } catch (e: Exception) { "Refresh failed" }
                    _uiState.value = _uiState.value.copy(
                        isRefreshingToken = false,
                        toastMessage = err
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshingToken = false,
                    toastMessage = "Network error — could not refresh token"
                )
            }
        }
    }

    private fun startExpiryCountdown(ttlMs: Long = 5 * 60 * 1000L) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(ttlMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                _uiState.value = _uiState.value.copy(
                    tokenExpiryText = "Token expires in %02d:%02d".format(minutes, seconds)
                )
            }
            override fun onFinish() {
                _uiState.value = _uiState.value.copy(
                    tokenExpiryText = "Token expired — tap Refresh",
                    toastMessage    = "NFC token expired. Please refresh."
                )
            }
        }.start()
    }

    fun onToastShown() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    suspend fun logout() {
        countDownTimer?.cancel()
        sessionDataStore.clearSession()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
