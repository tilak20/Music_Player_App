package com.example.music_player_app.DataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music")
data class Music(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "artist")
    val artist: String,
    @ColumnInfo(name = "data")
    val data: String,
    @ColumnInfo(name = "albumID")
    val albumID: Long,
    @ColumnInfo(name = "duration")
    val duration: Long,
    @ColumnInfo(name = "album")
    val album: String,
    @ColumnInfo(name = "albumArtUri")
    val albumArtUri: String,
    @ColumnInfo(name = "albumArt")
    val albumArt: String,
    @ColumnInfo(name = "dateAdded")
    val dateAdded: String,
    @ColumnInfo(name = "albumArtist")
    val albumArtist: String,
    @ColumnInfo(name = "genre")
    val genre: String,
    @ColumnInfo(name = "track")
    val track: String,
    @ColumnInfo(name = "numTrack")
    val numTrack: String,
    @ColumnInfo(name = "mimeType")
    val mimeType: String,
    @ColumnInfo(name = "size")
    val size: Int

)