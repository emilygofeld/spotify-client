package org.emily.auth.presentation.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.emily.auth.domain.authentication.AuthResult
import org.emily.auth.domain.repository.AuthRepository
import org.emily.auth.presentation.UiEvent

class AuthViewModel (
    private val repository: AuthRepository
): ViewModel() {
    private var _state by mutableStateOf(AuthState())
    val state = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        authenticate()
    }

    fun onEvent(event: AuthEvent) {
        when(event) {
            is AuthEvent.SignInUsernameChanged -> {
                _state = state.copy(signInUsername = event.value)
            }
            is AuthEvent.SignInPasswordChanged -> {
                _state = state.copy(signInPassword = event.value)
            }
            is AuthEvent.SignUpUsernameChanged -> {
                _state = state.copy(signUpUsername = event.value)
            }
            is AuthEvent.SignUpPasswordChanged -> {
                _state = state.copy(signUpPassword = event.value)
            }
            AuthEvent.SignIn -> {
                signIn()
            }
            AuthEvent.SignUp -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state = state.copy(isLoading = true)
            val result = repository.signUp(
                username = state.signUpUsername,
                password = state.signUpPassword
            )
            if (result is AuthResult.UnknownError) {
                _uiEvent.send(
                    UiEvent.ShowSnackBar(result.message ?: "Unknown Error")
                )
            }
            _state = state.copy(isLoading = false)
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state = state.copy(isLoading = true)
            val result = repository.signIn(
                username = state.signInUsername,
                password = state.signInPassword
            )
            if (result is AuthResult.UnknownError) {
                _uiEvent.send(
                    UiEvent.ShowSnackBar(result.message ?: "Unknown Error")
                )
            }
            _state = state.copy(isLoading = false)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            _state = state.copy(isLoading = true)
            val result = repository.authenticate()
            if (result is AuthResult.UnknownError) {
                _uiEvent.send(
                    UiEvent.ShowSnackBar(result.message ?: "Unknown Error")
                )
            }
            _state = state.copy(isLoading = false)
        }
    }
}