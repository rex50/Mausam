package com.rex50.mausam.storage.database.key_values

import android.content.Context
import com.rex50.mausam.storage.database.MausamRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class KeyValuesRepository {

    companion object{
        @JvmStatic
        suspend fun insert(context: Context?, key: String, value: String) = withContext(Dispatchers.IO) {
            context?.apply {
                MausamRoomDatabase.getDatabase(this)?.keyValuesDao()?.insert(KeyValues(key, value, DateTime.now()))
            }
        }

        @JvmStatic
        suspend fun update(context: Context?, key: String, value: String) = withContext(Dispatchers.IO) {
            context?.apply {
                MausamRoomDatabase.getDatabase(this)?.keyValuesDao()?.update(KeyValues(key, value, DateTime.now()))
            }
        }

        @JvmStatic
        suspend fun getValue(context: Context?, key: String?): String? = withContext(Dispatchers.IO) {
            return@withContext if(context != null) {
                MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.getValue(key)?.value
            }else
                null
        }

        @JvmStatic
        suspend fun checkValidityAndGetValue(context: Context?, key: String?, validityInHours: Int = 4): String? = withContext(Dispatchers.IO) {
            return@withContext if(context != null) {
                val value = MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.getValue(key)
                if(value?.dateCreated?.plusHours(validityInHours)?.isAfterNow == true){
                    value.value
                } else {
                    delete(context, key)
                    null
                }
            }else
                null
        }

        @JvmStatic
        suspend fun delete(context: Context?, key: String?) = withContext(Dispatchers.IO) {
            context?.apply {
                MausamRoomDatabase.getDatabase(this)?.keyValuesDao()?.delete(key)
            }
        }
    }
}