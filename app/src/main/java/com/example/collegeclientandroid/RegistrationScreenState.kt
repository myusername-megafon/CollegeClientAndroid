package com.example.collegeclientandroid

data class RegistrationScreenState(
    val fullName: String = "",
    val group: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)