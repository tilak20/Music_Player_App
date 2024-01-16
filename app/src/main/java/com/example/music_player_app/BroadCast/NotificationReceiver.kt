package com.example.music_player_app.BroadCast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import com.bumptech.glide.Glide
import com.example.music_player_app.Activity.Application
import com.example.music_player_app.Activity.Full_music
import com.example.music_player_app.Activity.MainActivity
import com.example.music_player_app.R
import com.example.music_player_app.Services.log
import java.io.IOException
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Application.PREVIOUS -> {
                if (Full_music.currentPosition == 0) {
                    Full_music.currentPosition = MainActivity.musicList.size
                    if (Full_music.mediaPlayer.isPlaying) {
                        Full_music.mediaPlayer.stop()
                        playAudioPrevious(context)
                    } else {
                        playAudioPrevious(context)
                    }
                } else {
                    if (Full_music.mediaPlayer.isPlaying) {
                        Full_music.mediaPlayer.stop()
                        playAudioPrevious(context)
                    } else {
                        playAudioPrevious(context)
                    }
                }
            }

            Application.PLAY -> {
                ("playing = 1 ==${Full_music.mediaPlayer.isPlaying}").log()
                Full_music.status = if (Full_music.status == 0) {
                    Full_music.binding.imgPlay.setImageResource(R.drawable.play)
                    pauseMusic()
                    1
                } else {
                    Full_music.binding.imgPlay.setImageResource(R.drawable.pause)
                    playMusic()
                    0
                }
            }

            Application.NEXT -> {
                if (Full_music.currentPosition == MainActivity.musicList.size) {
                    Full_music.currentPosition = 0
                    if (Full_music.mediaPlayer.isPlaying) {
                        Full_music.mediaPlayer.stop()
                        playAudioNext(context)
                    } else {
                        playAudioNext(context)
                    }
                } else {
                    if (Full_music.mediaPlayer.isPlaying) {
                        Full_music.mediaPlayer.stop()
                        playAudioNext(context)
                    } else {
                        playAudioNext(context)
                    }
                }
            }
            Application.CLOSE -> {
                Full_music.musicService!!.stopForeground(true)
                Full_music.mediaPlayer.stop()
            }
        }
    }


    fun playMusic() {

        if (Full_music.mediaPlayer.isPlaying) {
            resumePlayback()
        } else {

            Full_music.mediaPlayer.start()
            Full_music.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
            Full_music.binding.Lottie.resumeAnimation()
            Full_music.binding.imgPlay.setImageResource(R.drawable.pause)
        }
    }

    fun pauseMusic() {
        Full_music.mediaPlayer.pause()
        Full_music.binding.Lottie.pauseAnimation()
        Full_music.musicService!!.shownotification(R.drawable.baseline_play_arrow_24, 0f)
        Full_music.binding.imgPlay.setImageResource(R.drawable.play)
    }

    fun resumePlayback() {
        Full_music.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
        Full_music.mediaPlayer.start()
        Full_music.binding.Lottie.resumeAnimation()
    }

    fun playAudioPrevious(context: Context?) {
        try {
            Full_music.currentPosition--
            Full_music.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
            Full_music.binding.imgPlay.setImageResource(R.drawable.pause)
            Full_music.status = 0

            if (Full_music.currentPosition <= MainActivity.musicList.size) {
                playAudio(MainActivity.musicList[Full_music.currentPosition].data)

                Full_music.binding.title1.text =
                    MainActivity.musicList[Full_music.currentPosition].title

                Glide.with(context!!)
                    .load(MainActivity.musicList[Full_music.currentPosition].albumArt).into(
                        Full_music.binding.Thumbnail
                    )

                Full_music.binding.artist1.text =
                    MainActivity.musicList[Full_music.currentPosition].artist

                getDurationInMinutesSeconds(MainActivity.musicList[Full_music.currentPosition].duration.toInt())
            } else {
                Full_music.currentPosition = MainActivity.musicList.size
                val previousSong = MainActivity.musicList[Full_music.currentPosition].data
                playAudio(previousSong)
                Full_music.binding.title1.text =
                    MainActivity.musicList[Full_music.currentPosition].title

                Glide.with(context!!)
                    .load(MainActivity.musicList[Full_music.currentPosition].albumArt).into(
                        Full_music.binding.Thumbnail
                    )

                Full_music.binding.artist1.text =
                    MainActivity.musicList[Full_music.currentPosition].artist
                getDurationInMinutesSeconds(MainActivity.musicList[Full_music.currentPosition].duration.toInt())
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun playAudioNext(context: Context?) {

        try {
            Full_music.currentPosition++
            Full_music.binding.imgPlay.setImageResource(R.drawable.pause)
            Full_music.status = 0

            if (Full_music.currentPosition < MainActivity.musicList.size) {

                val nextSong = MainActivity.musicList[Full_music.currentPosition].data
                ("${Full_music.currentPosition < MainActivity.musicList.size}").log()

                playAudio(nextSong)
                Full_music.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)

                Full_music.binding.title1.text =
                    MainActivity.musicList[Full_music.currentPosition].title

                Glide.with(context!!)
                    .load(MainActivity.musicList[Full_music.currentPosition].albumArt).into(
                        Full_music.binding.Thumbnail
                    )

                Full_music.binding.artist1.text =
                    MainActivity.musicList[Full_music.currentPosition].artist
                getDurationInMinutesSeconds(MainActivity.musicList[Full_music.currentPosition].duration.toInt())
            } else {
                Full_music.currentPosition = 0
                val firstSong = MainActivity.musicList[Full_music.currentPosition].data
                ("first  == $firstSong").log()

                playAudio(firstSong)
                Full_music.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
                Full_music.binding.title1.text =
                    MainActivity.musicList[Full_music.currentPosition].title

                Glide.with(context!!)
                    .load(MainActivity.musicList[Full_music.currentPosition].albumArt).into(
                        Full_music.binding.Thumbnail
                    )

                Full_music.binding.artist1.text =
                    MainActivity.musicList[Full_music.currentPosition].artist

                getDurationInMinutesSeconds(MainActivity.musicList[Full_music.currentPosition].duration.toInt())

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    fun playAudio(nextSong: String) {


        if (Application.getBoolean("musicContinue")) {
            resumePlayback()
        } else {

            try {
                Full_music.mediaPlayer = MediaPlayer()
                Full_music.mediaPlayer.setDataSource(nextSong)
                Full_music.mediaPlayer.prepare()
                Full_music.mediaPlayer.start()
                Full_music.musicService!!.shownotification(R.drawable.baseline_pause_24, 1f)
                Full_music.binding.Lottie.visibility = View.VISIBLE
                Full_music.binding.seekID.max = Full_music.mediaPlayer.duration
                Full_music.binding.durationFinal.text
                progressChange()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun progressChange() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {


                Full_music.binding.seekID.progress = Full_music.mediaPlayer.currentPosition

                val minutes =
                    TimeUnit.MILLISECONDS.toMinutes(Full_music.mediaPlayer.currentPosition.toLong())
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(Full_music.mediaPlayer.currentPosition.toLong()) - TimeUnit.MINUTES.toSeconds(
                        minutes
                    )
                val duration1 =
                    getDurationInMinutesSeconds(MainActivity.musicList[Full_music.currentPosition].duration.toInt())
                val duration2 = "$minutes:$seconds"

//                ("duration == $duration2").log()


                Full_music.binding.duration.text = "$minutes:$seconds"
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    fun getDurationInMinutesSeconds(duration: Int): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        Full_music.binding.durationFinal.text = "$minutes:$seconds"
        return "$minutes:$seconds"
    }


}