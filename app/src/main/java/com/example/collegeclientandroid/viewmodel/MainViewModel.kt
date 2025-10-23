package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import com.example.collegeclientandroid.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {
    
    fun isUserLoggedIn(): Boolean {
        return authManager.isLoggedIn()
    }
}
