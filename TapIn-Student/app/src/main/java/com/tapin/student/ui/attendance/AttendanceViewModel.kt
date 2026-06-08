package com.tapin.student.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapin.student.data.remote.api.ApiService
import com.tapin.student.data.remote.dto.CourseAttendanceDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success(val courses: List<CourseAttendanceDto>) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val response = apiService.getMyAttendance()
                if (response.isSuccessful) {
                    _uiState.value = AttendanceUiState.Success(response.body()!!.data)
                } else {
                    _uiState.value = AttendanceUiState.Error(
                        "Failed to load attendance (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(
                    "Network error — check your connection"
                )
            }
        }
    }
}
