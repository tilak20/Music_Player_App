package com.example.music_player_app.Activity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.music_player_app.DataBase.Music
import com.example.music_player_app.DataBase.RoomDB
import com.example.music_player_app.R
import com.example.music_player_app.Services.MusicService
import com.example.music_player_app.Services.log
import com.example.music_player_app.databinding.ActivityFullMusicBinding
import dev.vivvvek.seeker.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class Full_music : AppCompatActivity(), ServiceConnection {


    var color2: Int = 0
    var lastprogress: Int = 0
    lateinit var Mintent: Intent
    lateinit var audioList1: List<Music>
    lateinit var uri: String

    companion object {
        var musicService: MusicService? = null
        var currentPosition: Int = 0
        lateinit var mediaPlayer: MediaPlayer
        var status: Int = 0

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityFullMusicBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFullMusicBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()

        volumeControlStream = AudioManager.STREAM_MUSIC

        Mintent = Intent(this, MusicService::class.java)
        bindService(Mintent, this, BIND_AUTO_CREATE)
        startService(Mintent)

        setIntentData()
        initClick()
        val database = RoomDB.getInstance(this@Full_music)
        audioList1 = database.musicDao().getMusicName()

        binding.title1.isSelected = true
        uri = audioList1[currentPosition].data

        if (mediaPlayer.isPlaying) {
        } else {
            binding.seekID.progress = 0
        }
        Seekbar()
    }

    fun initClick() {
        binding.imgPlay.setImageResource(R.drawable.pause)
        status = 0
        binding.imgForward.setOnClickListener {
            binding.seekID.progress = 0
            if (currentPosition == audioList1.size) {
                currentPosition = 0
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playAudioNext()
                } else {
                    playAudioNext()
                }
            } else {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playAudioNext()
                } else {
                    playAudioNext()
                }
            }
        }
        binding.imgRewind.setOnClickListener {
            ("playing = 4 == ${mediaPlayer.isPlaying}").log()
            binding.seekID.progress = 0

            if (currentPosition == 0) {
                currentPosition = audioList1.size
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playAudioPrevious()
                } else {
                    playAudioPrevious()
                }
            } else {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playAudioPrevious()
                } else {
                    playAudioPrevious()
                }
            }
        }
        binding.imgPlay.setOnClickListener {
            status = if (status == 0) {
                binding.imgPlay.setImageResource(R.drawable.play)
                ("playing = 2 == ${mediaPlayer.isPlaying}").log()
                pausePlayback()
                1
            } else {
                binding.imgPlay.setImageResource(R.drawable.pause)
                ("playing = 3 == ${mediaPlayer.isPlaying}").log()
                resumePlayback()
                0
            }
        }
    }


    fun setIntentData() {
        audioList1 = MainActivity.musicList


        currentPosition = intent.getIntExtra("position", 0)
        Glide.with(this).load(audioList1[currentPosition].albumArt).into(binding.Thumbnail)
        binding.artist1.text = audioList1[currentPosition].artist
        getDurationInMinutesSeconds(audioList1[currentPosition].duration.toInt())
        binding.title1.text = audioList1[currentPosition].title
    }

    fun getDurationInMinutesSeconds(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        binding.durationFinal.text = "$minutes:$seconds"
        return "$minutes:$seconds"
    }

    fun pausePlayback() {
        mediaPlayer.pause()
        musicService!!.shownotification(R.drawable.baseline_play_arrow_24, 0f)
        binding.Lottie.pauseAnimation()
        val currentPos = mediaPlayer.currentPosition
        mediaPlayer.seekTo(currentPos)
    }

    fun resumePlayback() {
        musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
        mediaPlayer.start()
        binding.Lottie.resumeAnimation()
    }

    @SuppressLint("SetTextI18n")
    fun playAudio(nextSong: String) {
        mediaPlayer = MediaPlayer()

        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            mediaPlayer.setDataSource(nextSong)

            mediaPlayer.prepare()
            mediaPlayer.start()
            musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)

            binding.Lottie.visibility = View.VISIBLE
            binding.seekID.max = mediaPlayer.duration
            binding.durationFinal.text
            progressChange()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("SetTextI18n")
    fun playAudioNext() {
        try {
            currentPosition++

            binding.imgPlay.setImageResource(R.drawable.pause)
            status = 0

            if (currentPosition < audioList1.size) {


                val nextSong = audioList1[currentPosition].data

                ("next == $nextSong").log()
                ("${currentPosition < audioList1.size}").log()

                playAudio(nextSong)
                musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)

                binding.title1.text = audioList1[currentPosition].title

                Glide.with(this).load(audioList1[currentPosition].albumArt).into(binding.Thumbnail)

                binding.artist1.text = audioList1[currentPosition].artist
                getDurationInMinutesSeconds(audioList1[currentPosition].duration.toInt())
            } else {
                currentPosition = 0
                val firstSong = audioList1[currentPosition].data
                ("first  == $firstSong").log()

                playAudio(firstSong)
                musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
                binding.title1.text = audioList1[currentPosition].title

                Glide.with(this).load(audioList1[currentPosition].albumArt).into(binding.Thumbnail)

                binding.artist1.text = audioList1[currentPosition].artist

                getDurationInMinutesSeconds(audioList1[currentPosition].duration.toInt())

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun playAudioPrevious() {
        try {
            currentPosition--
            musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)

            if (currentPosition <= audioList1.size) {

                binding.imgPlay.setImageResource(R.drawable.pause)
                status = 0

                val previousSong = audioList1[currentPosition].data

                playAudio(previousSong)

                binding.title1.text = audioList1[currentPosition].title

                Glide.with(this).load(audioList1[currentPosition].albumArt).into(binding.Thumbnail)

                binding.artist1.text = audioList1[currentPosition].artist

                getDurationInMinutesSeconds(audioList1[currentPosition].duration.toInt())
            } else {
                currentPosition = audioList1.size
                val previousSong = audioList1[currentPosition].data
                playAudio(previousSong)
                binding.title1.text = audioList1[currentPosition].title

                Glide.with(this).load(audioList1[currentPosition].albumArt).into(binding.Thumbnail)

                binding.artist1.text = audioList1[currentPosition].artist
                getDurationInMinutesSeconds(audioList1[currentPosition].duration.toInt())
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun Seekbar() {
        binding.seekID.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun progressChange() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {

                binding.seekID.progress = mediaPlayer.currentPosition

                val minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.currentPosition.toLong())
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.currentPosition.toLong()) - TimeUnit.MINUTES.toSeconds(
                        minutes
                    )
                val duration1 =
                    getDurationInMinutesSeconds(audioList1[currentPosition].duration.toInt())
                val duration2 = "$minutes:$seconds"
                if (duration1 == duration2) {
                    playAudioNext()
                }
                binding.duration.text = "$minutes:$seconds"
                handler.postDelayed(this, 1000)


            }
        }, 1000)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        lastprogress = mediaPlayer.currentPosition;
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        mediaPlayer = MediaPlayer()

//        ("connect").log()

        playAudio(uri)
        musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
        ("Connect Fail").log()
    }
}
