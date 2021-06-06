package com.rex50.mausam.storage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rex50.mausam.storage.database.key_values.KeyValues
import com.rex50.mausam.storage.database.key_values.KeyValuesDao
import com.rex50.mausam.utils.Converters

@Database(entities = [KeyValues::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MausamRoomDatabase : RoomDatabase() {
    abstract fun keyValuesDao(): KeyValuesDao?

    companion object {
        @Volatile
        private var INSTANCE: MausamRoomDatabase? = null
        fun getDatabase(context: Context): MausamRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(MausamRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room
                                .databaseBuilder(context.applicationContext, MausamRoomDatabase::class.java, "Mausam_Database") //remove below line and find a better solution
                                //.allowMainThreadQueries()
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}