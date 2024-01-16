package com.example.music_player_app.Activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.music_player_app.R
import com.example.music_player_app.databinding.ActivityDescriptionAct2Binding

class DescriptionAct2 : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionAct2Binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionAct2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initClick()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun initClick() {
        binding.button.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:$packageName")
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                startActivityForResult(intent, 1)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                startActivity(Intent(this, MainActivity::class.java))
                Application.putBoolean("open2", true)
                finish()


            } else {

                Toast.makeText(this, "Allow Permission", Toast.LENGTH_SHORT).show()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    val uri = Uri.parse("package:$packageName")
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    startActivityForResult(intent, 1)
                }
            }
        }
    }

}
