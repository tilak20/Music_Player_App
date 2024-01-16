package com.example.music_player_app.DataBase

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(music: Music)

    @Query("SELECT * FROM Music ORDER BY title ASC")
    fun getMusicName(): List<Music>

    @Query("SELECT * FROM Music ORDER BY dateAdded ASC")
    fun getMusicDate(): List<Music>

    @Query("SELECT * FROM Music ORDER BY artist ASC")
    fun getMusicArtist(): List<Music>

    @Delete
    fun deleteMusic(music: Music)

    @Query("DELETE FROM Music WHERE title = :name")
    fun deleteSongByName(name: String)

}