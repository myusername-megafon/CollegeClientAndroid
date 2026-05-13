package com.example.collegeclientandroid

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.example.collegeclientandroid.network.LoginRequest
import com.example.collegeclientandroid.network.NetworkModule
import com.example.collegeclientandroid.network.RegisterPushTokenRequest
import com.example.collegeclientandroid.network.RegisterRequest
import com.example.collegeclientandroid.network.VerifyEmailCodeRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                tryRegisterPushToken(loginResponse.group)
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
    ): RegistrationCodeRequestResult {
        return try {
            val response = NetworkModule.apiService.requestEmailCode(
                RegisterRequest(fullName, email, password, "", group)
            )
            if (response.isSuccessful) {
                Log.d("AuthManager", "Код подтверждения отправлен на email: $email")
                RegistrationCodeRequestResult.Success
            } else {
                val serverMessage = response.errorBody()?.string()?.trim().orEmpty()
                val message = when {
                    response.code() == 403 && serverMessage.isBlank() -> "Регистрация временно отключена администратором"
                    serverMessage.isNotBlank() -> serverMessage
                    else -> "Ошибка отправки кода (${response.code()})"
                }
                Log.e("AuthManager", "Ошибка отправки кода: ${response.code()} - $message")
                RegistrationCodeRequestResult.Failure(message)
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при отправке кода", e)
            RegistrationCodeRequestResult.Failure("Не удалось связаться с сервером. Проверьте интернет и адрес сервера")
        }
    }

    suspend fun verifyEmailCode(email: String, code: String): Boolean {
        return try {
            val response = NetworkModule.apiService.verifyEmailCode(
                VerifyEmailCodeRequest(email = email, code = code)
            )
            if (response.isSuccessful && response.body() != null) {
                val registerResponse = response.body()!!
                prefs.edit {
                    putInt("id", registerResponse.id)
                    putString("fio", registerResponse.fio)
                    putString("email", registerResponse.email)
                    putString("photo", registerResponse.photoFiletype ?: "")
                    putString("group", registerResponse.group ?: "")
                }
                tryRegisterPushToken(registerResponse.group)
                Log.d("AuthManager", "Почта подтверждена и сессия создана: $email")
                true
            } else {
                Log.e("AuthManager", "Ошибка подтверждения кода: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Исключение при подтверждении кода", e)
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

    private fun tryRegisterPushToken(group: String?) {
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            apiService.registerPushToken(
                                RegisterPushTokenRequest(
                                    token = token,
                                    group = group
                                )
                            )
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AuthManager", "Не удалось получить FCM token", e)
                }
        } catch (e: Exception) {
            Log.e("AuthManager", "FCM недоступен на устройстве", e)
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

sealed interface RegistrationCodeRequestResult {
    data object Success : RegistrationCodeRequestResult
    data class Failure(val message: String) : RegistrationCodeRequestResult
}