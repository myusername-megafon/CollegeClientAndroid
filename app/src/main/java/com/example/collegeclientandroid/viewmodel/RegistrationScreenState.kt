package com.example.collegeclientandroid.viewmodel

data class RegistrationScreenState(
    val fullName: String = "",
    val group: String = "",
    val email: String = "",
    val password: String = "",
    val verificationCode: String = "",
    val isCodeSent: Boolean = false,
    val cooldownSeconds: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isSuccess: Boolean = false,
)