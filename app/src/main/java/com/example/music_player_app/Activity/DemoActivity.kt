package com.example.music_player_app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.music_player_app.Audio1
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivityDemoBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DemoActivity : AppCompatActivity() {


    lateinit var binding: ActivityDemoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}