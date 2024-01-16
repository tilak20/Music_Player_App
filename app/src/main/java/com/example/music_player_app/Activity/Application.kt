package com.example.music_player_app.Activity

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build

class Application : Application() {


    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences(SHARED_KEY, MODE_PRIVATE)
        editor = preferences.edit()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Now Playing Song",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "This is important channel for showing song"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        var SHARED_KEY = "MusicShared"
        lateinit var preferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        val CHANNEL_ID = "channel1"
        val PLAY = "play"
        val NEXT = "next"
        val PREVIOUS = "previous"
        val CLOSE = "close"
        val OPEN = "open"

        fun putString(key: String, value: String) {
            editor.putString(key, value).apply()
        }

        fun getString(key: String): String {
            return preferences.getString(key, "").toString()
        }

        fun putBoolean(key: String, value: Boolean) {
            editor.putBoolean(key, value).apply()
        }

        fun getBoolean(key: String): Boolean {
            return preferences.getBoolean(key, false)
        }

        fun putInt(key: String, value: Int) {
            editor.putInt(key, value).apply()
        }

        fun getInt(key: String, i: Int): Int {
            return preferences.getInt(key, i)
        }



    }


}