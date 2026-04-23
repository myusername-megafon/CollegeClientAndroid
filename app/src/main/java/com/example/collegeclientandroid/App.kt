package com.example.collegeclientandroid

import android.app.Application
import com.example.collegeclientandroid.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
    }
