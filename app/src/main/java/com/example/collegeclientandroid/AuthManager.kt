package com.example.collegeclientandroid

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.collegeclientandroid.network.LoginRequest
import com.example.collegeclientandroid.network.NetworkModule
import com.example.collegeclientandroid.network.RegisterRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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

    fun getAuthorId(): Int {
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
            val userId = getAuthorId()
            if (userId == -1) {
                Log.e("AuthManager", "Пользователь не авторизован")
                return false
            }

            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
            
            val response = apiService.uploadUserPhoto(userId, photoPart)
            if (response.isSuccessful) {
                Log.d("AuthManager", "Фото успешно загружено")
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
            val userId = getAuthorId()
            if (userId == -1) {
                Log.e("AuthManager", "Пользователь не авторизован")
                return null
            }

            val response = apiService.getUserPhoto(userId)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.bytes()
            } else {
                Log.e("AuthManager", "Ошибка получения фото: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при получении фото", e)
            null
        }
    }
}

data class UserInfo(
    val id: Int,
    val fio: String,
    val email: String,
    val photo: String,
    val group: String
)