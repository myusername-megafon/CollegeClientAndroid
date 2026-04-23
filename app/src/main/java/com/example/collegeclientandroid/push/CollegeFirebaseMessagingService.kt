package com.example.collegeclientandroid.push

import com.example.collegeclientandroid.notifications.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CollegeFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var pushTokenManager: PushTokenManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        pushTokenManager.registerToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"] ?: "Уведомление колледжа"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        NotificationHelper.show(this, 2001, title, body)
    }
}
