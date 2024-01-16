package com.example.music_player_app.Activity

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.music_player_app.DataBase.Music
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivitySetRingtoneBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class SetRingtoneAct : AppCompatActivity() {

    var audioFileId: Long = 0
    lateinit var musicList: List<Music>
    var position: Int = 0
    lateinit var json: String
    lateinit var name: String
    lateinit var binding: ActivitySetRingtoneBinding
    var direct = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    var audioFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetRingtoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (Application.getString("setas")) {
            "phonering" -> binding.rdPhone.isChecked = true

            "notificationring" -> binding.rdNoti.isChecked = true

            "alarmring" -> binding.rdAlarm.isChecked = true

            else -> ("neither").log()
        }

        getIntentData()

        initClick()

    }

    private fun initClick() {

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }


        binding.rdGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rdPhone.id) {
                setRingtone(MediaStore.Audio.Media.IS_RINGTONE, RingtoneManager.TYPE_RINGTONE)
                Application.putString("setas", "phonering")
            } else if (checkedId == binding.rdNoti.id) {
                setRingtone(
                    MediaStore.Audio.Media.IS_NOTIFICATION,
                    RingtoneManager.TYPE_NOTIFICATION
                )
                Application.putString("setas", "notificationring")
            } else if (checkedId == binding.rdAlarm.id) {
                setRingtone(MediaStore.Audio.Media.IS_ALARM, RingtoneManager.TYPE_ALARM)
                Application.putString("setas", "alarmring")
            }
        }
    }

    fun setRingtone(isRingtone: String, typeRingtone: Int) {
        if (direct.listFiles()
                ?.any { it -> it.name.startsWith(musicList[position].title) } == true
        ) {
            direct.listFiles()?.forEach {
                if (it.name.startsWith(musicList[position].title)) {
                    name = it.name
                }
            }

            val values = ContentValues()
            values.put(isRingtone, true)
            try {
                getExternalMp3Files(name)
                val file = File(audioFilePath)
                val uri: Uri?
                if (file.exists()) {
                    val contentUriForPath =
                        MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
                    val strArr = arrayOf(file.absolutePath)
                    val contentUriForPath2 =
                        MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
                    val query = contentResolver.query(
                        contentUriForPath2!!,
                        arrayOf("_id"),
                        "_data=? ",
                        strArr,
                        null as String?
                    )
                    uri = if (query == null || !query.moveToFirst()) {
                        contentResolver.insert(contentUriForPath!!, values)
                    } else {
                        @SuppressLint("Range")
                        val valueOf = Integer.valueOf(
                            query.getInt(
                                query.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                            )
                        )
                        Uri.withAppendedPath(contentUriForPath, "" + valueOf)
                    }
                    RingtoneManager.setActualDefaultRingtoneUri(this, typeRingtone, uri)
                    Toast.makeText(this, "Ringtone Successfully Set", Toast.LENGTH_SHORT).show()
                }
            } catch (e2: java.lang.Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + this.packageName)
                startActivity(intent)
                Toast.makeText(this, "Unable To Set Ringtone", Toast.LENGTH_SHORT).show()
            }
        } else {
            ("Unable To Set Ringtone").log()
        }
    }


    fun getExternalMp3Files(name: String): Void? {
        val selection = MediaStore.Audio.Media.DISPLAY_NAME + "=?"
        val selectionArgs = arrayOf(name)

// Get the columns to retrieve
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,  // ID of the audio file
            MediaStore.Audio.Media.DATA // File path of the audio file
        )
        // Query the media store for the audio file
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,  // The content URI for audio media files
            projection,  // The columns to retrieve
            selection,  // The selection criteria
            selectionArgs,  // The value of the selection criteria
            null // The sort order for the results
        )
        // Check if the cursor returned any results
        if (cursor != null && cursor.moveToFirst()) {
            // Get the ID and file path of the audio file from the cursor
            audioFileId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            audioFilePath =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

            // Log the ID and file path of the audio file
            Log.d("FATZ", "Audio file ID: $audioFileId")
            Log.d("FATZ", "Audio file path: $audioFilePath")
            // Close the cursor
            cursor.close()
        }
        return null
    }


    fun getIntentData() {
        json = intent.getStringExtra("size").toString()
        position = intent.getIntExtra("position", 0)
        musicList = MainActivity.musicList

        ("list == ${musicList[position].title}").log()
    }
}