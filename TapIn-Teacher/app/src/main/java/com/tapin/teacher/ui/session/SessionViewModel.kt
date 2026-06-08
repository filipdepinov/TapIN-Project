package com.tapin.teacher.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapin.teacher.data.local.entity.AttendanceRecord
import com.tapin.teacher.data.remote.dto.SessionDto
import com.tapin.teacher.data.repository.AttendanceRepository
import com.tapin.teacher.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val sessionId: String?            = null,
    val courseId: String              = "",
    val courseName: String            = "",
    val courseCode: String            = "",
    val sessionStatus: String         = "open",
    val records: List<AttendanceRecord> = emptyList(),
    val isStartingSession: Boolean    = true,
    val isNfcReady: Boolean           = false,
    val isClosingSession: Boolean     = false,
    val lastTapMessage: String?       = null,
    val lastTapSuccess: Boolean       = true,
    val toastMessage: String?         = null,
    val sessionClosed: Boolean        = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: AttendanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String   = savedStateHandle["courseId"]   ?: ""
    private val courseName: String = savedStateHandle["courseName"] ?: ""
    private val courseCode: String = savedStateHandle["courseCode"] ?: ""

    private val _uiState = MutableStateFlow(
        SessionUiState(courseId = courseId, courseName = courseName, courseCode = courseCode)
    )
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        startSession()
    }

    private fun startSession() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isStartingSession = true)
            when (val result = repository.createSession(courseId)) {
                is Result.Success -> {
                    val session = result.data
                    _uiState.value = _uiState.value.copy(
                        sessionId        = session.id,
                        isStartingSession = false,
                        isNfcReady       = true
                    )
                    observeLocalRecords(session.id)
                }
                is Result.Error -> {
                    // If a session is already open (409), try to get it
                    if (result.code == 409) {
                        fetchOpenSession()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isStartingSession = false,
                            toastMessage      = "Failed to start session: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    private suspend fun fetchOpenSession() {
        when (val result = repository.getSessions()) {
            is Result.Success -> {
                val openSession = result.data.firstOrNull {
                    it.courseId == courseId && it.status == "open"
                }
                if (openSession != null) {
                    _uiState.value = _uiState.value.copy(
                        sessionId        = openSession.id,
                        isStartingSession = false,
                        isNfcReady       = true,
                        toastMessage     = "Resumed existing open session"
                    )
                    observeLocalRecords(openSession.id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isStartingSession = false,
                        toastMessage      = "Could not start or resume session"
                    )
                }
            }
            is Result.Error -> _uiState.value = _uiState.value.copy(
                isStartingSession = false,
                toastMessage      = result.message
            )
        }
    }

    private fun observeLocalRecords(sessionId: String) {
        viewModelScope.launch {
            repository.getSessionRecordsFlow(sessionId).collect { records ->
                _uiState.value = _uiState.value.copy(records = records)
            }
        }
    }

    /**
     * Called when the teacher's phone reads an NFC tap from a student device.
     * The raw NDEF payload (base64 encrypted token) is passed here.
     */
    fun onNfcTap(encryptedToken: String) {
        val sessionId = _uiState.value.sessionId ?: return
        viewModelScope.launch {
            val result = repository.validateAndRecord(
                encryptedToken = encryptedToken,
                sessionId      = sessionId,
                courseName     = courseName,
                courseCode     = courseCode
            )
            when (result) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    lastTapMessage = "${result.data.student.fullName} registered!",
                    lastTapSuccess = true
                )
                is Result.Error -> {
                    val friendly = when {
                        result.code == 409  -> "Student already present in this session"
                        result.code == 401  -> "Token expired — ask student to refresh"
                        result.message.contains("closed") -> "Session is closed"
                        else                -> result.message
                    }
                    _uiState.value = _uiState.value.copy(
                        lastTapMessage = friendly,
                        lastTapSuccess = false
                    )
                }
            }
        }
    }

    fun closeSession() {
        val sessionId = _uiState.value.sessionId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClosingSession = true)
            when (val result = repository.closeSession(sessionId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isClosingSession = false,
                    sessionStatus    = "closed",
                    isNfcReady       = false,
                    sessionClosed    = true,
                    toastMessage     = "Session closed — ${_uiState.value.records.size} students recorded"
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isClosingSession = false,
                    toastMessage     = "Failed to close session: ${result.message}"
                )
            }
        }
    }

    fun onTapMessageShown() { _uiState.value = _uiState.value.copy(lastTapMessage = null) }
    fun onToastShown()      { _uiState.value = _uiState.value.copy(toastMessage = null) }
}
