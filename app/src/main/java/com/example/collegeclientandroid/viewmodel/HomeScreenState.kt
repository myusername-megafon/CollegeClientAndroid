package com.example.collegeclientandroid.viewmodel

import com.example.collegeclientandroid.AuthManager

data class HomeScreenState(
    val groupName: String = "",
    val selectedDate: String = "",
    val selectedDateMillis: Long? = null,
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val schedule: List<String> = emptyList()
)