package com.example.collegeclientandroid.push

import android.util.Log
import com.example.collegeclientandroid.AuthManager
import com.example.collegeclientandroid.network.ApiService
import com.example.collegeclientandroid.network.RegisterPushTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushTokenManager @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun registerCurrentDeviceToken() {
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    registerToken(token)
                }
                .addOnFailureListener { e ->
                    Log.e("PushTokenManager", "FCM token error", e)
                }
        } catch (e: Exception) {
            // Do not break app startup if Google services are unavailable on emulator.
            Log.e("PushTokenManager", "FCM unavailable on this device", e)
        }
    }

    fun registerToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                apiService.registerPushToken(
                    RegisterPushTokenRequest(
                        token = token,
                        group = authManager.getGroupName()
                    )
                )
            }.onFailure {
                Log.e("PushTokenManager", "register token failed", it)
            }
        }
    }
}
