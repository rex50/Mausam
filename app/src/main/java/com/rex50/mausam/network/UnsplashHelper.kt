package com.rex50.mausam.network

import android.content.Context
import androidx.annotation.IntRange
import androidx.annotation.StringDef
import com.android.volley.Request
import com.rex50.mausam.interfaces.GetUnsplashCollectionsAndTagsListener
import com.rex50.mausam.interfaces.GetUnsplashPhotosAndUsersListener
import com.rex50.mausam.interfaces.GetUnsplashPhotosListener
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener
import com.rex50.mausam.network.APIManager.UnsplashAPICallResponse
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import com.rex50.mausam.utils.Constants.ApiConstants.COLLECTION_ID
import com.rex50.mausam.utils.Constants.ApiConstants.DOWNLOADING_PHOTO_URL
import com.rex50.mausam.utils.Constants.ApiConstants.UNSPLASH_USERNAME
import com.rex50.mausam.utils.DataParser
import kotlinx.coroutines.*
import org.json.JSONArray
import java.util.*

class UnsplashHelper {

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(ORDER_BY_DEFAULT, ORDER_BY_LATEST, ORDER_BY_OLDEST, ORDER_BY_POPULAR)
    private annotation class OrderPhotosByRestriction

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(ORIENTATION_UNSPECIFIED, ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT, ORIENTATION_SQUARISH)
    private annotation class PhotosOrientationRestriction

    @Retention(AnnotationRetention.SOURCE)
    @IntRange(from = 1, to = 20)
    private annotation class PerPageRestriction

    private var apiManager: APIManager? = null
    private var context: Context? = null
    private var defaultOrientation = ORIENTATION_PORTRAIT

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private constructor()

    constructor(context: Context?) {
        this.context = context
        apiManager = APIManager.getInstance(context)
    }

    constructor(context: Context?, @PhotosOrientationRestriction defaultOrientation: String) {
        this.context = context
        apiManager = APIManager.getInstance(context)
        this.defaultOrientation = defaultOrientation
    }

    fun getPhotosAndUsers(@OrderPhotosByRestriction orderBy: String, listener: GetUnsplashPhotosAndUsersListener) {
        getPhotosAndUsers(orderBy, 1, 10, defaultOrientation, listener)
    }

    fun getPhotosAndUsers(@OrderPhotosByRestriction orderBy: String, page: Int, @PerPageRestriction perPage: Int, listener: GetUnsplashPhotosAndUsersListener) {
        getPhotosAndUsers(orderBy, page, perPage, defaultOrientation, listener)
    }

    fun getPhotosAndUsers(@OrderPhotosByRestriction orderBy: String, page: Int, @PerPageRestriction perPage: Int, @PhotosOrientationRestriction orientation: String, listener: GetUnsplashPhotosAndUsersListener) {
        val extras = HashMap<String, String>()
        extras["order_by"] = orderBy
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        if (!orientation.equals(ORIENTATION_UNSPECIFIED, ignoreCase = true)) extras["orientation"] = orientation
        context?.apply {
            scope.launch {
                val response = KeyValuesRepository.checkValidityAndGetValue(context, PHOTOS_KEY + orderBy + page + perPage, RESPONSE_VALIDITY_HOURS)
                response?.takeIf { it.isNotEmpty() }?.apply {
                    val photosAndUsers = DataParser.parseUnsplashData(response, true)
                    listener.onSuccess(photosAndUsers.photosList, photosAndUsers.userList)
                } ?: apply {
                    apiManager?.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS, extras, object : UnsplashAPICallResponse {
                        override fun onSuccess(response: String) {
                            val photosAndUsers = DataParser.parseUnsplashData(response, true)
                            listener.onSuccess(photosAndUsers.photosList, photosAndUsers.userList)
                            scope.launch {
                                KeyValuesRepository.insert(context, PHOTOS_KEY + orderBy + page + perPage, response)
                            }
                        }

                        override fun onFailed(errors: JSONArray) {
                            listener.onFailed(errors)
                        }
                    }) ?: listener.onFailed(JSONArray())
                }
            }
        } ?: listener.onFailed(JSONArray())
    }

    /*public void getPhotos(@OrderPhotosByRestriction String orderBy, GetUnsplashPhotosListener listener){
        APIManager apiManager = APIManager.getInstance(context);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("order_by", orderBy);
        String response = KeyValuesRepository.getValue(context, orderBy);
        if(response == null || response.isEmpty()){
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    //prefs.setPhotosResponseMap(orderBy, response);
                    List<UnsplashPhotos> photosList = DataParser.parseUnsplashData(response, false).getPhotosList();
                    KeyValuesRepository.insert(context, orderBy, response);

                    Log.e(TAG, "getPhotos: getting from API");
                    listener.onSuccess(photosList);
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            Log.e(TAG, "getPhotos: getting from sharedPrefs");
            listener.onSuccess(DataParser.parseUnsplashData(response, false).getPhotosList());
        }
    }*/
    fun getSearchedPhotos(searchTerm: String?, page: Int, @PerPageRestriction perPage: Int, listener: GetUnsplashSearchedPhotosListener) {
        getSearchedPhotos(searchTerm, page, perPage, defaultOrientation, listener)
    }

    fun getSearchedPhotos(searchTerm: String?, page: Int, @PerPageRestriction perPage: Int, @PhotosOrientationRestriction photosOrientation: String, listener: GetUnsplashSearchedPhotosListener) {
        val extras = HashMap<String, String>()
        extras["query"] = searchTerm ?: ""
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        if (photosOrientation != ORIENTATION_UNSPECIFIED) extras["orientation"] = photosOrientation
        context?.apply {
            scope.launch {
                val response = KeyValuesRepository.checkValidityAndGetValue(context, SEARCHED_PHOTOS_KEY + searchTerm + page + perPage, RESPONSE_VALIDITY_HOURS)
                response?.takeIf { it.isNotEmpty() }?.let {res ->
                    listener.onSuccess(DataParser.parseSearchedPhotos(res))
                } ?: apply {
                    apiManager?.makeUnsplashRequest(APIManager.SERVICE_GET_SEARCHED_PHOTOS, extras, object : UnsplashAPICallResponse {
                        override fun onSuccess(response: String) {
                            val photos = DataParser.parseSearchedPhotos(response)
                            listener.onSuccess(photos)
                            scope.launch {
                                KeyValuesRepository.insert(context, SEARCHED_PHOTOS_KEY + searchTerm + page + perPage, response)
                            }
                        }

                        override fun onFailed(errors: JSONArray) {
                            listener.onFailed(errors)
                        }
                    }) ?: listener.onFailed(JSONArray())
                }
            }
        } ?: listener.onFailed(JSONArray())
    }

    fun getCollectionPhotos(collectionId: String, listener: GetUnsplashSearchedPhotosListener) {
        getSearchedPhotos(collectionId, 1, 20, defaultOrientation, listener)
    }

    fun getCollectionPhotos(collectionId: String?, page: Int, @PerPageRestriction perPage: Int, listener: GetUnsplashPhotosListener) {
        val extras = HashMap<String, String>()
        extras[COLLECTION_ID] = collectionId ?: ""
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        context?.apply {
            scope.launch {
                val response = KeyValuesRepository.checkValidityAndGetValue(context, COLLECTION_PHOTOS_KEY + collectionId + page + perPage, RESPONSE_VALIDITY_HOURS)
                response?.takeIf { it.isNotEmpty() }?.apply {
                    listener.onSuccess(DataParser.parseUnsplashData(response, false).photosList)
                } ?: apply {
                    apiManager?.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTION_PHOTOS, extras, object : UnsplashAPICallResponse {
                        override fun onSuccess(response: String) {
                            val photosAndUsers = DataParser.parseUnsplashData(response, false)
                            listener.onSuccess(photosAndUsers.photosList)
                            scope.launch {
                                KeyValuesRepository.insert(context, COLLECTION_PHOTOS_KEY + collectionId + page + perPage, response)
                            }
                        }

                        override fun onFailed(errors: JSONArray) {
                            listener.onFailed(errors)
                        }
                    }) ?: listener.onFailed(JSONArray())
                }
            }
        } ?: listener.onFailed(JSONArray())
    }

    fun getCollectionsAndTags(page: Int, @PerPageRestriction perPage: Int, listener: GetUnsplashCollectionsAndTagsListener) {
        val extras = HashMap<String, String>()
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        context?.apply {
            scope.launch {
                val response = KeyValuesRepository.checkValidityAndGetValue(context, COLLECTION_KEY + page + perPage, RESPONSE_VALIDITY_HOURS)
                response?.takeIf { it.isNotEmpty() }?.apply {
                    val obj = DataParser.parseCollections(response, true)
                    listener.onSuccess(obj.collectionsList, obj.tagsList)
                }?: apply {
                    apiManager?.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTIONS, extras, object : UnsplashAPICallResponse {
                        override fun onSuccess(response: String) {
                            val obj = DataParser.parseCollections(response, true)
                            listener.onSuccess(obj.collectionsList, obj.tagsList)
                            scope.launch {
                                KeyValuesRepository.insert(context, COLLECTION_KEY + page + perPage, response)
                            }
                        }

                        override fun onFailed(errors: JSONArray) {
                            listener.onFailed(errors)
                        }
                    }) ?: listener.onFailed(JSONArray())
                }
            }
        } ?: listener.onFailed(JSONArray())
    }

    fun getUserPhotos(unsplashUserName: String?, page: Int, @PerPageRestriction perPage: Int, listener: GetUnsplashPhotosListener) {
        val extras = HashMap<String, String>()
        extras[UNSPLASH_USERNAME] = unsplashUserName ?: ""
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        context?.apply {
            scope.launch {
                val response = KeyValuesRepository.checkValidityAndGetValue(context, PHOTOGRAPHER_KEY + unsplashUserName + page + perPage, RESPONSE_VALIDITY_HOURS)
                response?.takeIf{ it.isNotEmpty() }?.apply{
                    val photosAndUsers = DataParser.parseUnsplashData(response, true)
                    listener.onSuccess(photosAndUsers.photosList)
                }?: apply {
                    apiManager?.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS_BY_USER, extras, object : UnsplashAPICallResponse {
                        override fun onSuccess(response: String) {
                            val photosAndUsers = DataParser.parseUnsplashData(response, true)
                            listener.onSuccess(photosAndUsers.photosList)
                            scope.launch {
                                KeyValuesRepository.insert(context, PHOTOGRAPHER_KEY + unsplashUserName + page + perPage, response)
                            }
                        }

                        override fun onFailed(errors: JSONArray) {
                            listener.onFailed(errors)
                        }
                    })?: listener.onFailed(JSONArray())
                }
            }
        } ?: listener.onFailed(JSONArray())
    }

    fun getUserCollections(unsplashUserName: String?, page: Int, @PerPageRestriction perPage: Int, listener: GetUnsplashCollectionsAndTagsListener) {
        val extras = HashMap<String, String>()
        extras[UNSPLASH_USERNAME] = unsplashUserName ?: ""
        extras["page"] = if (page < 1) "1" else page.toString()
        extras["per_page"] = perPage.toString()
        context?.apply {
            scope.launch {
                val response = KeyValuesRepository.checkValidityAndGetValue(context!!, COLLECTION_KEY + unsplashUserName + page + perPage, RESPONSE_VALIDITY_HOURS)
                response?.takeIf { it.isNotEmpty() }?.apply {
                    val obj = DataParser.parseCollections(response, true)
                    listener.onSuccess(obj.collectionsList, obj.tagsList)
                }?: apply {
                    apiManager?.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTIONS_BY_USER, extras, object : UnsplashAPICallResponse {
                        override fun onSuccess(response: String) {
                            val obj = DataParser.parseCollections(response, true)
                            listener.onSuccess(obj.collectionsList, obj.tagsList)
                            scope.launch {
                                KeyValuesRepository.insert(context, COLLECTION_KEY + unsplashUserName + page + perPage, response)
                            }
                        }

                        override fun onFailed(errors: JSONArray) {
                            listener.onFailed(errors)
                        }
                    })?: listener.onFailed(JSONArray())
                }
            }
        }?: listener.onFailed(JSONArray())
    }

    fun trackDownload(url: String) {
        val extras = HashMap<String, String>()
        extras[DOWNLOADING_PHOTO_URL] = url
        apiManager?.makeUnsplashRequest(APIManager.SERVICE_POST_DOWNLOADING_PHOTO, extras, Request.Method.GET, null)
    }

    companion object {

        private const val TAG = "UnsplashHelper"

        private const val PHOTOGRAPHER_KEY = "key_photographer_"
        private const val COLLECTION_KEY = "key_collection_"
        private const val COLLECTION_PHOTOS_KEY = "key_collection_photo_"
        private const val PHOTOS_KEY = "key_photos_"
        private const val SEARCHED_PHOTOS_KEY = "key_searched_photo_"

        //Unsplash order by Constants
        const val ORDER_BY_DEFAULT = "latest"
        const val ORDER_BY_LATEST = "latest"
        const val ORDER_BY_OLDEST = "oldest"
        const val ORDER_BY_POPULAR = "popular"

        //Unsplash orientation Constants
        const val ORIENTATION_UNSPECIFIED = "unspecified"
        const val ORIENTATION_LANDSCAPE = "landscape"
        const val ORIENTATION_PORTRAIT = "portrait"
        const val ORIENTATION_SQUARISH = "squarish"

        const val RESPONSE_VALIDITY_HOURS = 4
    }
}