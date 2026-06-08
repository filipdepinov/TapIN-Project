package com.tapin.teacher.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapin.teacher.data.local.entity.AttendanceRecord
import com.tapin.teacher.data.repository.AttendanceRepository
import com.tapin.teacher.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionGroup(
    val sessionId: String,
    val courseName: String,
    val courseCode: String,
    val records: List<AttendanceRecord>,
    val earliestTap: Long
)

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val sessions: List<SessionGroup>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            repository.getAllRecordsFlow()
                .map { records ->
                    records
                        .groupBy { it.sessionId }
                        .map { (sid, recs) ->
                            SessionGroup(
                                sessionId   = sid,
                                courseName  = recs.first().courseName,
                                courseCode  = recs.first().courseCode,
                                records     = recs.sortedBy { it.tappedAt },
                                earliestTap = recs.minOf { it.tappedAt }
                            )
                        }
                        .sortedByDescending { it.earliestTap }
                }
                .collect { sessions ->
                    _uiState.value = HistoryUiState.Success(sessions)
                }
        }
    }
}
