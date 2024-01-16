package com.example.music_player_app.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bumptech.glide.Glide
import com.example.music_player_app.*
import com.example.music_player_app.Adapter.MusicAdapter
import com.example.music_player_app.DataBase.Dao
import com.example.music_player_app.DataBase.Music
import com.example.music_player_app.DataBase.RoomDB
import com.example.music_player_app.Dialog.DeleteDialog
import com.example.music_player_app.Dialog.ExitDialog
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivityMainBinding
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseAct<ActivityMainBinding>(), PopupMenu.OnMenuItemClickListener {

    lateinit var it1: View
    lateinit var popupMenu: PopupMenu
    lateinit var name: String
    lateinit var mimeType: String
    lateinit var numTrack: String
    lateinit var track: String
    lateinit var genre: String
    lateinit var albumArtist: String
    var position: Int = 0
    lateinit var musictitle: String
    lateinit var data: String

    companion object {
        lateinit var musicList: List<Music>
    }

    lateinit var albumArt: Bitmap
    lateinit var database: Dao
    lateinit var musicAdapter: MusicAdapter
    val searchList = arrayListOf<Music>()
    var status = 0
    lateinit var permissions: Array<String>

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initUI() {

        requestPermissions()
        database = RoomDB.getInstance(this@MainActivity).musicDao()
        musicList = database.getMusicName()
        val mp3Count = countMp3Files()
        if (database.getMusicName().size < mp3Count) {
            binding.Lottie1.visibility = View.VISIBLE

            Thread {
                getMusic()
                runOnUiThread {
                    binding.RCView.isVerticalScrollBarEnabled = true
                    RcView(musicList)
                    status = 1
                }
            }.start()

        } else if (database.getMusicName().size > mp3Count) {
            val roomDatabase = Room.databaseBuilder(this, RoomDB::class.java, "Music")
                .fallbackToDestructiveMigration().build()
            val supportDB: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase
            supportDB.execSQL("DELETE FROM Music")
            roomDatabase.close()

            binding.Lottie1.visibility = View.VISIBLE

            Thread {
                getMusic()
                runOnUiThread {
                    binding.RCView.isVerticalScrollBarEnabled = true
                    RcView(musicList)
                    status = 1
                }
            }.start()
        } else {
            RcView(musicList)
        }
        initClick()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    fun showPopupMenu(anchorView: View, sortMenu: Int) {
        popupMenu = PopupMenu(this, anchorView, Gravity.START, 0, R.style.MyPopupMenu)
        popupMenu.menuInflater.inflate(sortMenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Name -> {
                binding.txtSort.text = "Name"
                binding.txtSort.animation = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
                musicList = database.getMusicName()
                RcView(musicList)
                return true
            }
            R.id.DateAdded -> {
                binding.txtSort.text = "Date Added"
                binding.txtSort.animation = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
                musicList = database.getMusicDate()
                RcView(musicList)
                return true
            }
            R.id.Artist -> {
                binding.txtSort.text = "Artist"
                binding.txtSort.animation = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
                musicList = database.getMusicArtist()
                RcView(musicList)
                return true
            }

            R.id.Details -> {
                startActivity(Intent(this, DetailsAct::class.java).putExtra("position", position))
                return true
            }
            R.id.Delete -> {
                val file = File(musicList[position].data)
                DeleteDialog(this, file) {
                    database.deleteSongByName(musicList[position].title)
                }
                return true
            }
            R.id.Share -> {
                ("Share").log()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "audio/mp3"
                val uri = Uri.parse(musicList[position].data)
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, "Share audio file"))
                return true
            }
            R.id.SetAs -> {
                startActivity(
                    Intent(this, SetRingtoneAct::class.java)
                        .putExtra("position", position)
                )
                return true
            }
        }
        return false
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
                ("$requestCode").log()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun countMp3Files(): Int {
        val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE)
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )
        var count = 0

        if (cursor != null && cursor.moveToFirst()) {

            val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            do {
                musictitle = cursor.getString(titleColumn)
                data = cursor.getString(dataColumn)
                if (data.endsWith(".mp3")) {
                    count += 1
                }
            } while (cursor.moveToNext())


        }
        cursor?.close()
        return count
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getMusic() {

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.NUM_TRACKS,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE
        )

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        if (cursor != null && cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val albumArtistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val numTrackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.NUM_TRACKS)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            do {
                val id = cursor.getLong(idColumn)
                val musictitle = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val data = cursor.getString(dataColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val duration = cursor.getLong(durationColumn)
                val album = cursor.getString(albumColumn)
                val dateAdded = cursor.getString(dateAddedColumn)
                val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")


                ("path == $data == $")

                albumArtist = if (cursor.getString(albumArtistColumn) != null) {
                    cursor.getString(albumArtistColumn)
                } else {
                    "Unknown"
                }

                genre = if (cursor.getString(genreColumn) != null) {
                    cursor.getString(genreColumn)
                } else {
                    "Unknown"
                }

                track = if (cursor.getString(trackColumn) != null) {
                    cursor.getString(trackColumn)
                } else {
                    "Unknown"
                }

                mimeType = if (cursor.getString(mimeTypeColumn) != null) {
                    cursor.getString(mimeTypeColumn)
                } else {
                    "Unknown"
                }

                numTrack = if (cursor.getString(numTrackColumn) != null) {
                    cursor.getString(numTrackColumn)
                } else {
                    "Unknown"
                }

                val size = cursor.getInt(sizeColumn)
                albumArt = try {
                    MediaStore.Images.Media.getBitmap(contentResolver, albumArtUri)
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(resources, R.drawable.musical_note)
                }


                val name = "image_" + System.currentTimeMillis() + ".jpg"
                val file = File(this.cacheDir, name)
                val outputStream = FileOutputStream(file)
                albumArt.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                val uri = FileProvider.getUriForFile(this, "com.example.app.fileprovider", file)

                val music = Music(
                    id, musictitle, artist, data, albumId, duration, album, albumArtUri.toString(),
                    uri.toString(), dateAdded, albumArtist, genre, track, numTrack, mimeType, size
                )
                if (data.endsWith(".mp3")) {
                    database.insertMusic(music)
                }
                musicList = database.getMusicName()

            } while (cursor.moveToNext())

        }

        cursor?.close()
    }

    fun initClick() {


        binding.imgSort.setOnClickListener {
            showPopupMenu(binding.imgSort, R.menu.sort_menu)
        }
        binding.txtSort.setOnClickListener {
            showPopupMenu(binding.imgSort, R.menu.sort_menu)
        }
        binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                searchList.clear()


                for (i in musicList) {
                    if (i.title.lowercase()
                            .contains(newText!!.lowercase(Locale.getDefault()))
                    ) {
                        searchList.add(i)
                    }
                }
                musicAdapter.SearchData(searchList)
                return true
            }
        })
    }

    fun RcView(musicList: List<Music>) {
        binding.Lottie1.visibility = View.GONE
        musicAdapter = MusicAdapter(this@MainActivity, musicList) { it, pos ->
            position = pos
            it1 = it
            showPopupMenu(it, R.menu.option_menu)
        }

        val scaleInAnimationAdapter = ScaleInAnimationAdapter(musicAdapter)
        scaleInAnimationAdapter.setDuration(500)
        scaleInAnimationAdapter.setInterpolator(OvershootInterpolator())
        scaleInAnimationAdapter.setFirstOnly(false)
        binding.RCView.adapter = scaleInAnimationAdapter

    }

    override fun onBackPressed() {
        ExitDialog(this@MainActivity) {
            super.onBackPressed()
        }
    }

}