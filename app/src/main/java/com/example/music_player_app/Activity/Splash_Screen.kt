package com.example.music_player_app.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.music_player_app.Audio
import com.example.music_player_app.R
import com.example.music_player_app.Services.log
import com.google.gson.Gson

class Splash_Screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({

            if (Application.getBoolean("open1")) {
                startActivity(Intent(this, MainActivity::class.java))
            } else if (Application.getBoolean("open2")) {
                startActivity(Intent(this, DescriptionAct2::class.java))
            } else {
                startActivity(Intent(this, DescriptionActivity::class.java))
            }
            finish()

        }, 3000)


    }


}