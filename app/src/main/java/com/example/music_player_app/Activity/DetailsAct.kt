package com.example.music_player_app.Activity

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.DatabaseUtils
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.music_player_app.DataBase.Music
import com.example.music_player_app.R
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivityDetailsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.Nullable


class DetailsAct : AppCompatActivity() {

    var position: Int = 0

    companion object {
        lateinit var audioList1: List<Music>
    }

    lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ("AMin == ${MainActivity.musicList.size}").log()

        binding.CrdView1.setBackgroundResource(R.drawable.card_background)
        getIntentData()
        setIntentData()

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

    }


    fun setIntentData() {
        val pattern = "yyyy-MM-dd"

        Glide.with(this).load(audioList1[position].albumArt).into(binding.imgThumbnail)
        binding.txtTitle.text = audioList1[position].title
        binding.txtArtist.text = audioList1[position].artist
        binding.txtAlbum.text = audioList1[position].album
        binding.txtPath.text = audioList1[position].data
        binding.txtSize.text = formatFileSize(audioList1[position].size)
        binding.txtAlbumArtist.text = audioList1[position].albumArtist
        binding.txtFormat.text = audioList1[position].mimeType
        binding.txtTrackNumber.text = audioList1[position].numTrack
        binding.txtGenre.text = audioList1[position].genre
        binding.txtRecordingDate.text =
            convertTimestampToDateString(audioList1[position].dateAdded.toLong(), pattern)

        val minutes = audioList1[position].duration / 1000 / 60
        val seconds = audioList1[position].duration / 1000 % 60
        binding.txtTrackLength.text = "$minutes:$seconds"

    }

    fun convertTimestampToDateString(timestamp: Long, pattern: String): String {
        val date = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat(pattern)
        return dateFormat.format(date)
    }

    fun formatFileSize(size: Int): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        val formattedSize = String.format("%.2f", size / Math.pow(1024.0, digitGroups.toDouble()))

        return "$formattedSize ${units[digitGroups]}"
    }

    fun getIntentData() {
        position = intent.getIntExtra("position", 0)
        audioList1 = MainActivity.musicList

        ("list == ${audioList1[position].title}").log()
    }


}