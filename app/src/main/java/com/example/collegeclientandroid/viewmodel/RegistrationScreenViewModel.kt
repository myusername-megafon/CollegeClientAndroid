package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeclientandroid.AuthManager
import com.example.collegeclientandroid.RegistrationCodeRequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _screenState: MutableStateFlow<RegistrationScreenState> =
        MutableStateFlow(RegistrationScreenState())
    val screenState: StateFlow<RegistrationScreenState> = _screenState.asStateFlow()
    private var cooldownJob: Job? = null

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

    fun onVerificationCodeChange(value: String) {
        _screenState.value = _screenState.value.copy(verificationCode = value, errorMessage = null)
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
            _screenState.value = _screenState.value.copy(isLoading = true, errorMessage = null, infoMessage = null)
            try {
                when (val result = authManager.register(
                    state.fullName,
                    state.email,
                    state.password,
                    state.group
                )) {
                    RegistrationCodeRequestResult.Success -> {
                        _screenState.value = _screenState.value.copy(
                            isCodeSent = true,
                            infoMessage = "Код подтверждения отправлен на email",
                            cooldownSeconds = 30
                        )
                        startCooldown()
                    }
                    is RegistrationCodeRequestResult.Failure -> {
                        _screenState.value = _screenState.value.copy(errorMessage = result.message)
                    }
                }
            } catch (t: Throwable) {
                _screenState.value = _screenState.value.copy(errorMessage = t.message ?: "Ошибка отправки кода")
            } finally {
                _screenState.value = _screenState.value.copy(isLoading = false)
            }
        }
    }

    fun resendCode() {
        val state = _screenState.value
        if (!state.isCodeSent) return
        if (state.cooldownSeconds > 0) return

        viewModelScope.launch {
            _screenState.value = state.copy(isLoading = true, errorMessage = null, infoMessage = null)
            try {
                when (val result = authManager.register(
                    state.fullName,
                    state.email,
                    state.password,
                    state.group
                )) {
                    RegistrationCodeRequestResult.Success -> {
                        _screenState.value = _screenState.value.copy(
                            infoMessage = "Код отправлен повторно",
                            cooldownSeconds = 30
                        )
                        startCooldown()
                    }
                    is RegistrationCodeRequestResult.Failure -> {
                        _screenState.value = _screenState.value.copy(errorMessage = result.message)
                    }
                }
            } catch (t: Throwable) {
                _screenState.value = _screenState.value.copy(errorMessage = t.message ?: "Ошибка повторной отправки кода")
            } finally {
                _screenState.value = _screenState.value.copy(isLoading = false)
            }
        }
    }

    fun closeVerification() {
        cooldownJob?.cancel()
        _screenState.value = _screenState.value.copy(
            isCodeSent = false,
            verificationCode = "",
            cooldownSeconds = 0,
            infoMessage = null,
            errorMessage = null
        )
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            while (_screenState.value.cooldownSeconds > 0) {
                delay(1000)
                _screenState.value = _screenState.value.copy(
                    cooldownSeconds = (_screenState.value.cooldownSeconds - 1).coerceAtLeast(0)
                )
            }
        }
    }

    fun verifyCode() {
        val state = _screenState.value
        if (!state.isCodeSent) {
            _screenState.value = state.copy(errorMessage = "Сначала запросите код")
            return
        }
        if (state.verificationCode.length < 4) {
            _screenState.value = state.copy(errorMessage = "Введите код из письма")
            return
        }

        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(isLoading = true, errorMessage = null, infoMessage = null)
            try {
                val success = authManager.verifyEmailCode(state.email, state.verificationCode)
                if (success) {
                    _screenState.value = _screenState.value.copy(isSuccess = true)
                } else {
                    _screenState.value = _screenState.value.copy(errorMessage = "Неверный код или срок действия истек")
                }
            } catch (t: Throwable) {
                _screenState.value = _screenState.value.copy(errorMessage = t.message ?: "Ошибка подтверждения кода")
            } finally {
                _screenState.value = _screenState.value.copy(isLoading = false)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}