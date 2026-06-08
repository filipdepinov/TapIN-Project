package com.tapin.teacher.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tapin.teacher.data.local.SessionDataStore
import com.tapin.teacher.data.remote.api.ApiService
import com.tapin.teacher.data.remote.dto.ApiError
import com.tapin.teacher.data.remote.dto.LoginRequest
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
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = apiService.login(LoginRequest(email.trim(), password))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    if (body.user.role !in listOf("teacher", "admin")) {
                        _uiState.value = LoginUiState.Error(
                            "This app is for teachers only. Please use the Student App."
                        )
                        return@launch
                    }
                    sessionDataStore.saveSession(
                        jwt      = body.token,
                        userId   = body.user.id,
                        userName = body.user.fullName,
                        email    = body.user.email
                    )
                    _uiState.value = LoginUiState.Success
                } else {
                    val msg = try {
                        Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error
                    } catch (e: Exception) { "Login failed (${response.code()})" }
                    _uiState.value = LoginUiState.Error(msg)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Cannot connect to server. Check your network.")
            }
        }
    }
}
