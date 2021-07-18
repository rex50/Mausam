package com.rex50.mausam.storage.database.key_values

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.storage.database.MausamRoomDatabase
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.toArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File

class KeyValuesRepository(private var context: Context) {

    suspend fun getDownloadedPhotos(): LiveData<ArrayList<UnsplashPhotos>>? = withContext(Dispatchers.IO) {

        val scope = CoroutineScope(Dispatchers.IO)

        return@withContext searchStartingWith(Constants.Image.DOWNLOADED_IMAGE)?.map { list ->
            KeyValueDataMapper.mapJsonListToUnsplashPhotosList(list).filter {
                File(it.relativePath).exists().also { exists ->
                    if(!exists) {
                        scope.launch { delete(it.dbId) }
                    }
                }
            }.toArrayList()
        }
    }

    suspend fun checkValidityAndGetValue(key: String?, validityInHours: Int = 4): String? = withContext(Dispatchers.IO) {
        return@withContext run {
            val value = MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.getValue(key)
            if(value?.dateCreated?.plusHours(validityInHours)?.isAfterNow == true){
                value.value
            } else {
                delete(key)
                null
            }
        }
    }

    suspend fun insert(key: String, value: String) = withContext(Dispatchers.IO) {
        MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.insert(KeyValues(key, value, DateTime.now()))
    }

    suspend fun delete(key: String?) = withContext(Dispatchers.IO) {
        MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.delete(key)
    }

    suspend fun searchStartingWith(searchTerm: String?): LiveData<List<KeyValues>>? = withContext(Dispatchers.IO) {
        return@withContext MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.searchSimilarTo("$searchTerm%")
    }

    suspend fun getValue(key: String?): String? = withContext(Dispatchers.IO) {
        return@withContext MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.getValue(key)?.value
    }

    suspend fun update(key: String, value: String) = withContext(Dispatchers.IO) {
        MausamRoomDatabase.getDatabase(context)?.keyValuesDao()?.update(KeyValues(key, value, DateTime.now()))
    }


    companion object{

        const val TAG = "KeyValuesRepository"

    }
}