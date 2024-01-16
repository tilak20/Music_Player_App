package com.example.music_player_app.Activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.music_player_app.Dialog.PermissionDialogAct
import com.example.music_player_app.R
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivityDescriptionBinding

class DescriptionActivity : AppCompatActivity() {

    lateinit var permissions: Array<String>
    lateinit var binding: ActivityDescriptionBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClick()
        requestPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun initClick() {
        binding.button.setOnClickListener {

            requestPermissions()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                startActivity(Intent(this, MainActivity::class.java))
                Application.putBoolean("open1", true)
            } else {
                if (Application.getBoolean("open2")) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Application.putBoolean("open1", true)
                    startActivity(Intent(this, DescriptionAct2::class.java))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions() {
        permissions = arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 100)
        } else {
            ("Done").log()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                PermissionDialogAct(this@DescriptionActivity) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {

                        startActivity(Intent(this, DescriptionAct2::class.java))
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}

