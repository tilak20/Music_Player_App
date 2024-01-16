package com.example.music_player_app

import android.graphics.Bitmap

data class Audio(
    val title: String,
    val artist: String,
    val album: String,
    val duration: String?,
    val absolutePath: String,
    val bitmap: Bitmap,
)
data class Audio1(
    val id: Long,
    val musictitle: String,
    val artist: String,
    val data: String,
    val duration: Long,
    val album: String,
    val albumArt: Bitmap
)

