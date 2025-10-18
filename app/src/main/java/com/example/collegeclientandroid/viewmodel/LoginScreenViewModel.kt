package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    // добавить зависимости позже
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

        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(isLoading = true, errorMessage = null)
            try {
                _screenState.value = _screenState.value.copy(isSuccess = true)
            } catch (t: Throwable) {
                _screenState.value = _screenState.value.copy(errorMessage = t.message ?: "Ошибка входа")
            } finally {
                _screenState.value = _screenState.value.copy(isLoading = false)
            }
        }
    }
}


