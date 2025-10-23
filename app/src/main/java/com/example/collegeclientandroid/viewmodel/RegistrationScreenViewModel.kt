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
class RegistrationScreenViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _screenState: MutableStateFlow<RegistrationScreenState> =
        MutableStateFlow(RegistrationScreenState())
    val screenState: StateFlow<RegistrationScreenState> = _screenState.asStateFlow()

    fun onFullNameChange(value: String) {
        _screenState.value = _screenState.value.copy(fullName = value, errorMessage = null)
    }

    fun onGroupChange(value: String) {
        _screenState.value = _screenState.value.copy(group = value, errorMessage = null)
    }

    fun onEmailChange(value: String) {
        _screenState.value = _screenState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _screenState.value = _screenState.value.copy(password = value, errorMessage = null)
    }

    fun register() {
        val state = _screenState.value
        if (state.fullName.isBlank() || state.group.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _screenState.value = state.copy(errorMessage = "Заполните все поля")
            return
        }

        if (!isValidEmail(state.email)) {
            _screenState.value = state.copy(errorMessage = "Введите корректный email")
            return
        }

        if (state.password.length < 6) {
            _screenState.value = state.copy(errorMessage = "Пароль должен содержать минимум 6 символов")
            return
        }

        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(isLoading = true, errorMessage = null)
            try {
                val success = authManager.register(
                    state.fullName,
                    state.email,
                    state.password,
                    state.group
                )
                if (success) {
                    _screenState.value = _screenState.value.copy(isSuccess = true)
                } else {
                    _screenState.value = _screenState.value.copy(errorMessage = "Ошибка регистрации. Возможно, пользователь с таким email уже существует")
                }
            } catch (t: Throwable) {
                _screenState.value = _screenState.value.copy(errorMessage = t.message ?: "Ошибка регистрации")
            } finally {
                _screenState.value = _screenState.value.copy(isLoading = false)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}