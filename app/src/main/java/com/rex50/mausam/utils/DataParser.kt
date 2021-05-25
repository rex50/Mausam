package com.rex50.mausam.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.CollectionsAndTags
import com.rex50.mausam.model_classes.utils.PhotosAndUsers
import com.rex50.mausam.model_classes.weather.WeatherModelClass
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object DataParser {

    const val TAG = "DataParser"

    @JvmStatic
    fun parseWeatherData(response: JSONObject): WeatherModelClass {
        val gson = Gson()
        return gson.fromJson(response.toString(), WeatherModelClass::class.java)
    }

    fun parseUnsplashData(response: String?, parseUsersList: Boolean): PhotosAndUsers{
        val gson = Gson()
        val photos: MutableList<UnsplashPhotos> = ArrayList()
        val users: MutableList<User> = ArrayList()
        try {
            val responseArray = JSONArray(response)
            if (parseUsersList) {
                for (i in 0 until responseArray.length()) {
                    val obj = gson.fromJson(responseArray.getJSONObject(i).toString(), UnsplashPhotos::class.java)
                    photos.add(obj)
                    users.add(obj.user)
                }
            } else {
                for (i in 0 until responseArray.length()) {
                    photos.add(gson.fromJson(responseArray.getJSONObject(i).toString(), UnsplashPhotos::class.java))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return PhotosAndUsers(photos, users)
    }

    fun parseSearchedPhotos(response: String?): SearchedPhotos {
        val gson = Gson()
        return gson.fromJson(response, SearchedPhotos::class.java)
    }

    fun parseCollections(response: String?, parseTagsList: Boolean): CollectionsAndTags {
        val gson = Gson()
        val collections: MutableList<Collections> = ArrayList()
        val tags: MutableSet<Tag> = HashSet()
        try {
            val responseArray = JSONArray(response)
            if (parseTagsList) {
                for (i in 0 until responseArray.length()) {
                    try {
                        val obj = gson.fromJson(responseArray.getJSONObject(i).toString(), Collections::class.java)
                        collections.add(obj)
                        tags.addAll(obj.tags)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "parseCollections: response: ${responseArray.getJSONObject(i)}", e)
                    }
                }
            } else {
                for (i in 0 until responseArray.length()) {
                    collections.add(gson.fromJson(responseArray.getJSONObject(i).toString(), Collections::class.java))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return CollectionsAndTags(collections, ArrayList(tags))
    }
}