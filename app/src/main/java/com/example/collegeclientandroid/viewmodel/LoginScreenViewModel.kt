package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeclientandroid.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    val authManager: AuthManager
) : ViewModel() {

    private val _screenState: MutableStateFlow<LoginScreenState> = MutableStateFlow(LoginScreenState())
    val screenState: StateFlow<LoginScreenState> = _screenState.asStateFlow()

    fun onEmailChange(value: String) {
        _screenState.value = _screenState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _screenState.value = _screenState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val state = _screenState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _screenState.value = state.copy(errorMessage = "Заполните все поля")
            return
        }

        if (!isValidEmail(state.email)) {
            _screenState.value = state.copy(errorMessage = "Введите корректный email")
            return
        }

        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(isLoading = true, errorMessage = null)
            try {
                val success = authManager.logIn(state.email, state.password)
                if (success) {
                    _screenState.value = _screenState.value.copy(isSuccess = true)
                } else {
                    _screenState.value = _screenState.value.copy(errorMessage = "Неверный email или пароль")
                }
            } catch (t: Throwable) {
                _screenState.value = _screenState.value.copy(errorMessage = t.message ?: "Ошибка входа")
            } finally {
                _screenState.value = _screenState.value.copy(isLoading = false)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}


