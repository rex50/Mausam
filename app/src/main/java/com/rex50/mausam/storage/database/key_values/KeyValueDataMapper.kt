package com.rex50.mausam.storage.database.key_values

import android.util.Log
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos

object KeyValueDataMapper {

    fun mapJsonListToUnsplashPhotosList(jsonList: List<KeyValues>): ArrayList<UnsplashPhotos> {
        val photosList = arrayListOf<UnsplashPhotos>()
        jsonList.forEach { keyValue ->
            try {
                UnsplashPhotos.getModelFromJSON(keyValue.value)?.let { photosList.add(it) }
            } catch (e: Exception) {
                Log.e(KeyValuesRepository.TAG, "mapDownloadedPhotos: ", e)
            }
        }
        return photosList
    }
}