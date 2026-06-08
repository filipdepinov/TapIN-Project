package com.tapin.student.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tapin.student.data.local.SessionDataStore
import com.tapin.student.data.remote.api.ApiService
import com.tapin.student.data.remote.dto.ApiError
import com.tapin.student.data.remote.dto.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle    : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email and password are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = apiService.login(LoginRequest(email.trim(), password))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    if (body.user.role != "student") {
                        _uiState.value = LoginUiState.Error(
                            "This app is for students only. Please use the Teacher App."
                        )
                        return@launch
                    }
                    sessionDataStore.saveSession(
                        jwt       = body.token,
                        nfcToken  = body.nfcToken,
                        userId    = body.user.id,
                        userName  = body.user.fullName,
                        userEmail = body.user.email,
                        studentId = body.user.studentId
                    )
                    _uiState.value = LoginUiState.Success
                } else {
                    val errBody = response.errorBody()?.string()
                    val message = try {
                        Gson().fromJson(errBody, ApiError::class.java).error
                    } catch (e: Exception) {
                        "Login failed (${response.code()})"
                    }
                    _uiState.value = LoginUiState.Error(message)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    "Cannot connect to server. Check your network connection."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
