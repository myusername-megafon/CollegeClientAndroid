package com.example.collegeclientandroid.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeclientandroid.AuthManager
import com.example.collegeclientandroid.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {
    
    private val _userPhoto = MutableStateFlow<Bitmap?>(null)
    val userPhoto: StateFlow<Bitmap?> = _userPhoto.asStateFlow()
    
    private val _isLoadingPhoto = MutableStateFlow(false)
    val isLoadingPhoto: StateFlow<Boolean> = _isLoadingPhoto.asStateFlow()
    
    private val _photoError = MutableStateFlow<String?>(null)
    
    fun getUserInfo(): UserInfo? {
        return authManager.getUserInfo()
    }
    
    fun logout() {
        authManager.logout()
    }
    
    fun loadUserPhoto() {
        viewModelScope.launch {
            _isLoadingPhoto.value = true
            _photoError.value = null
            
            try {
                val photoBytes = authManager.getUserPhoto()
                if (photoBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                    _userPhoto.value = bitmap
                } else {
                    _photoError.value = "Фото не найдено"
                }
            } catch (e: Exception) {
                _photoError.value = "Ошибка загрузки фото: ${e.message}"
            } finally {
                _isLoadingPhoto.value = false
            }
        }
    }
    
    fun uploadUserPhoto(photoFile: File) {
        viewModelScope.launch {
            _isLoadingPhoto.value = true
            _photoError.value = null
            
            try {
                val success = authManager.uploadUserPhoto(photoFile)
                if (success) {
                    loadUserPhoto()
                } else {
                    _photoError.value = "Ошибка загрузки фото на сервер"
                }
            } catch (e: Exception) {
                _photoError.value = "Ошибка загрузки фото: ${e.message}"
            } finally {
                _isLoadingPhoto.value = false
            }
        }
    }
}
