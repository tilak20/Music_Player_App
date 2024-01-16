package com.example.music_player_app.Services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.music_player_app.Activity.Application
import com.example.music_player_app.Activity.Full_music
import com.example.music_player_app.Activity.MainActivity
import com.example.music_player_app.BroadCast.NotificationReceiver
import com.example.music_player_app.R
import com.google.api.ResourceProto.resource
import java.io.IOException

class MusicService : Service() {


    //    lateinit var notificationManager: NotificationManager
    val myBinder = MyBinder()
    lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }


    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun shownotification(playButton: Int, playbackspeed: Float) {

        val preIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.PREVIOUS)
        val prevPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, preIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.PLAY)
        val playPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.NEXT)
        val nextPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val closeIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.CLOSE)
        val closePendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, closeIntent, PendingIntent.FLAG_IMMUTABLE)

        val premultipliedLargeIcon =
            if (getBitmapFromUri(MainActivity.musicList[Full_music.currentPosition].albumArt.toUri()) == null) {
                Bitmap.createBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.musical_note
                    )
                )
            } else {
                getBitmapFromUri(MainActivity.musicList[Full_music.currentPosition].albumArt.toUri())!!
            }


        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


//        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//
//        val metadataBuilder = MediaMetadataCompat.Builder()
//        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Full_music.mediaPlayer.duration.toLong())
//
//        val mStateBuilder = PlaybackStateCompat.Builder()
//            .setState(PlaybackStateCompat.STATE_PLAYING, 1, 1.0f)
//            .setBufferedPosition(Full_music.mediaPlayer.duration.toLong())
//            .setActions(
//                PlaybackStateCompat.ACTION_PLAY or
//                        PlaybackStateCompat.ACTION_PAUSE or
//                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
//                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
//                        PlaybackStateCompat.ACTION_SEEK_TO or
//                        PlaybackStateCompat.ACTION_PLAY_PAUSE
//            )
//
//
//        mediaSession.setMetadata(metadataBuilder.build())
//        mediaSession.setPlaybackState(mStateBuilder.build())
//
//
//
//        mediaSession.setMetadata(
//            MediaMetadataCompat.Builder()
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, premultipliedLargeIcon)
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, MainActivity.musicList[Full_music.currentPosition].artist)
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, MainActivity.musicList[Full_music.currentPosition].album)
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, MainActivity.musicList[Full_music.currentPosition].title)
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, MainActivity.musicList[Full_music.currentPosition].duration)
//                .build()
//        )


        val notification = NotificationCompat.Builder(baseContext, Application.CHANNEL_ID)
            .setContentTitle(MainActivity.musicList[Full_music.currentPosition].title)
            .setContentText(MainActivity.musicList[Full_music.currentPosition].artist)
            .setSmallIcon(R.drawable.music_note)
            .setLargeIcon(premultipliedLargeIcon)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", prevPendingIntent)
            .addAction(playButton, "Play", playPendingIntent)
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextPendingIntent)
            .addAction(R.drawable.baseline_close_24, "Close", closePendingIntent)


//        notificationManager.notify(13, notification.build())

        startForeground(13, notification.build())

    }


    fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


}