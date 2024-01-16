package com.example.music_player_app.Services

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun String.log() = Log.wtf("FATZ",this)

fun isServiceRunning(ctx: Context, serviceClass: Class<*>): Boolean {
    for (service in (ctx.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
        Int.MAX_VALUE
    )) {
        if (serviceClass.name == service.service.className) return true
    }
    return false
}