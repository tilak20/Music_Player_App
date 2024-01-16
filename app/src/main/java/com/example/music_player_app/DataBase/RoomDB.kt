package com.example.music_player_app.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Music::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun musicDao(): Dao

    companion object {
        @Volatile
        var INSTANCE: RoomDB? = null
        var dbName = "Music"

        fun getInstance(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, RoomDB::class.java, dbName).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}