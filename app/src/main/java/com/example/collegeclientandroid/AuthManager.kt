package com.example.collegeclientandroid

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.example.collegeclientandroid.network.LoginRequest
import com.example.collegeclientandroid.network.NetworkModule
import com.example.collegeclientandroid.network.RegisterRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val apiService = NetworkModule.apiService

    fun isLoggedIn(): Boolean {
        return prefs.getInt("id", -1) != -1
    }

    fun getGroupName(): String {
        return prefs.getString("group", null) ?: ""
    }

    suspend fun logIn(email: String, password: String): Boolean {
        return try {
            val response = NetworkModule.apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                prefs.edit {
                    putInt("id", loginResponse.id)
                    putString("fio", loginResponse.fio)
                    putString("email", loginResponse.email)
                    putString("photo", loginResponse.photo)
                    putString("group", loginResponse.group)
                }
                Log.d("AuthManager", "Успешный вход для пользователя: ${loginResponse.fio}")
                true
            } else {
                Log.e("AuthManager", "Ошибка входа: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при входе", e)
            false
        }
    }

    fun getUserId(): Int {
        return prefs.getInt("id", -1)
    }

    fun getUserInfo(): UserInfo? {
        val id = prefs.getInt("id", -1)
        if (id == -1) return null
        
        return UserInfo(
            id = id,
            fio = prefs.getString("fio", "") ?: "",
            email = prefs.getString("email", "") ?: "",
            photo = prefs.getString("photo", "") ?: "",
            group = prefs.getString("group", "") ?: ""
        )
    }

    fun logout() {
        prefs.edit {
            remove("id")
            remove("fio")
            remove("email")
            remove("photo")
            remove("group")
            remove("user_photo_base64")
        }
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        group: String
    ): Boolean {
        return try {
            val response = NetworkModule.apiService.registration(
                RegisterRequest(fullName, email, password, "", group)
            )
            if (response.isSuccessful && response.body() != null) {
                val registerResponse = response.body()!!
                Log.d("AuthManager", "Успешная регистрация для пользователя: ${registerResponse.fio}")
                true
            } else {
                Log.e("AuthManager", "Ошибка регистрации: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при регистрации", e)
            false
        }
    }

    suspend fun uploadUserPhoto(photoFile: File): Boolean {
        return try {
            val userId = getUserId()
            if (userId == -1) {
                Log.e("AuthManager", "Пользователь не авторизован")
                return false
            }

            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
            
            val response = apiService.uploadUserPhoto(userId, photoPart)
            if (response.isSuccessful) {
                Log.d("AuthManager", "Фото успешно загружено")
                val photoBytes = photoFile.readBytes()
                saveUserPhotoToPrefs(photoBytes)
                true
            } else {
                Log.e("AuthManager", "Ошибка загрузки фото: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при загрузке фото", e)
            false
        }
    }

    suspend fun getUserPhoto(): ByteArray? {
        return try {
            val userId = getUserId()
            if (userId == -1) {
                Log.e("AuthManager", "Пользователь не авторизован")
                return null
            }

            val response = apiService.getUserPhoto(userId)
            if (response.isSuccessful && response.body() != null) {
                val photoBytes = response.body()!!.bytes()
                saveUserPhotoToPrefs(photoBytes)
                photoBytes
            } else {
                Log.e("AuthManager", "Ошибка получения фото: ${response.code()} - ${response.message()}")
                getUserPhotoFromPrefs()
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при получении фото", e)
            getUserPhotoFromPrefs()
        }
    }
    
    private fun saveUserPhotoToPrefs(photoBytes: ByteArray) {
        try {
            val base64Photo = Base64.encodeToString(photoBytes, Base64.DEFAULT)
            prefs.edit {
                putString("user_photo_base64", base64Photo)
            }
            Log.d("AuthManager", "Фото сохранено в преференсы")
        } catch (e: Exception) {
            Log.e("AuthManager", "Ошибка сохранения фото в преференсы", e)
        }
    }
    
    fun getUserPhotoFromPrefs(): ByteArray? {
        return try {
            val base64Photo = prefs.getString("user_photo_base64", null)
            if (base64Photo != null) {
                val photoBytes = Base64.decode(base64Photo, Base64.DEFAULT)
                Log.d("AuthManager", "Фото загружено из преференсов")
                photoBytes
            } else {
                Log.d("AuthManager", "Фото не найдено в преференсах")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Ошибка загрузки фото из преференсов", e)
            null
        }
    }
    
    fun clearUserPhotoFromPrefs() {
        prefs.edit {
            remove("user_photo_base64")
        }
        Log.d("AuthManager", "Фото удалено из преференсов")
    }
}

data class UserInfo(
    val id: Int,
    val fio: String,
    val email: String,
    val photo: String,
    val group: String
)