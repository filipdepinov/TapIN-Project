package com.tapin.teacher.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapin.teacher.data.local.SessionDataStore
import com.tapin.teacher.data.remote.dto.CourseDto
import com.tapin.teacher.data.repository.AttendanceRepository
import com.tapin.teacher.data.repository.Result
import com.tapin.teacher.sync.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val teacherName: String       = "",
    val courses: List<CourseDto>  = emptyList(),
    val pendingSyncCount: Int     = 0,
    val isLoadingCourses: Boolean = true,
    val isSyncing: Boolean        = false,
    val syncProgress: Int         = 0,
    val toastMessage: String?     = null,
    val errorMessage: String?     = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AttendanceRepository,
    private val sessionDataStore: SessionDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTeacherName()
        loadCourses()
        observePendingSync()
    }

    private fun loadTeacherName() {
        viewModelScope.launch {
            sessionDataStore.userName.collect { name ->
                _uiState.value = _uiState.value.copy(teacherName = name ?: "")
            }
        }
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCourses = true, errorMessage = null)
            when (val result = repository.getCourses()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    courses = result.data,
                    isLoadingCourses = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoadingCourses = false,
                    errorMessage = result.message
                )
            }
        }
    }

    private fun observePendingSync() {
        viewModelScope.launch {
            repository.getPendingSyncCount().collect { count ->
                _uiState.value = _uiState.value.copy(pendingSyncCount = count)
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, syncProgress = 0)
            val result = repository.syncPendingRecords { pct ->
                _uiState.value = _uiState.value.copy(syncProgress = pct)
            }
            when (result) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isSyncing     = false,
                    syncProgress  = 100,
                    toastMessage  = "Sync complete — ${result.data} records uploaded"
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isSyncing    = false,
                    toastMessage = "Sync failed: ${result.message}"
                )
            }
        }
    }

    fun onToastShown() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    suspend fun logout() {
        sessionDataStore.clearSession()
    }
}
