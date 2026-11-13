package com.nivara.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nivara.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val message: String = "") : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val authStateFlow = AuthRepository.observeAuthState()

    fun signIn(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter email and password")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = AuthRepository.signIn(email, pass)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success("Login successful")
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = AuthRepository.signUp(email, pass, name)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success("Account created successfully")
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email address")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = AuthRepository.sendPasswordReset(email)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success("Password reset email sent")
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "Failed to send reset email")
            }
        }
    }

    fun signOut() {
        AuthRepository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
