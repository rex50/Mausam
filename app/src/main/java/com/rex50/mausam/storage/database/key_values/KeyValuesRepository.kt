package com.rex50.mausam.storage.database.key_values

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.storage.database.MausamRoomDatabase
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.toArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class KeyValuesRepository {

    companion object{

        const val TAG = "KeyValuesRepository"

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

        @JvmStatic
        suspend fun searchStartingWith(context: Context?, searchTerm: String?): LiveData<List<KeyValues>>? = withContext(Dispatchers.IO) {
            return@withContext if(context != null) {
                MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.searchSimilarTo("$searchTerm%")
            } else
                null
        }

        @JvmStatic
        suspend fun getDownloadedPhotos(context: Context?): LiveData<ArrayList<UnsplashPhotos>>? = withContext(Dispatchers.IO) {
            return@withContext searchStartingWith(context, Constants.Image.DOWNLOADED_IMAGE)?.map { list ->
                parseDownloadedPhotos(list.toArrayList())
            }
        }

        @JvmStatic
        private fun parseDownloadedPhotos(jsonList: ArrayList<KeyValues>): ArrayList<UnsplashPhotos> {
            val photosList = arrayListOf<UnsplashPhotos>()
            jsonList.forEach { keyValue ->
                try {
                    UnsplashPhotos.getModelFromJSON(keyValue.value)?.let { photosList.add(it) }
                } catch (e: Exception) {
                    Log.e(TAG, "parseDownloadedPhotos: ", e)
                }
            }
            return photosList
        }
    }
}