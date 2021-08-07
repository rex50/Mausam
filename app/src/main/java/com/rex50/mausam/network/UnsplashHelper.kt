package com.rex50.mausam.network

import androidx.annotation.IntRange
import androidx.annotation.StringDef
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.CollectionsAndTags
import com.rex50.mausam.model_classes.utils.PhotosAndUsers
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import com.rex50.mausam.utils.ConnectionChecker
import com.rex50.mausam.utils.Constants.ApiConstants.DOWNLOADING_PHOTO_URL
import com.rex50.mausam.utils.Constants.Network.NO_INTERNET
import com.rex50.mausam.utils.DataParser
import com.rex50.mausam.utils.isNotNullOrEmpty
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import java.util.*

class UnsplashHelper(
    private val keyValuesRepository: KeyValuesRepository,
    private val apiManager: APIManager,
    val connectionChecker: ConnectionChecker,
) {

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(ORDER_BY_DEFAULT, ORDER_BY_LATEST, ORDER_BY_OLDEST, ORDER_BY_POPULAR)
    private annotation class OrderPhotosByRestriction

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(ORIENTATION_UNSPECIFIED, ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT, ORIENTATION_SQUARISH)
    private annotation class PhotosOrientationRestriction

    @Retention(AnnotationRetention.SOURCE)
    @IntRange(from = 1, to = 20)
    private annotation class PerPageRestriction

    @PhotosOrientationRestriction
    var defaultOrientation = ORIENTATION_PORTRAIT

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    suspend fun getPhotosAndUsers(@OrderPhotosByRestriction orderBy: String): Result<PhotosAndUsers> {
        return getPhotosAndUsers(orderBy, 1, 10, defaultOrientation)
    }

    suspend fun getPhotosAndUsers(@OrderPhotosByRestriction orderBy: String, page: Int, @PerPageRestriction perPage: Int): Result<PhotosAndUsers> {
        return getPhotosAndUsers(orderBy, page, perPage, defaultOrientation)
    }

    suspend fun getPhotosAndUsers(
        @OrderPhotosByRestriction orderBy: String,
        page: Int,
        @PerPageRestriction perPage: Int,
        @PhotosOrientationRestriction orientation: String,
    ): Result<PhotosAndUsers> = withContext(Dispatchers.IO)  {

        val dbKey = PHOTOS_KEY + orderBy + page + perPage

        //Check if data available locally and not expired
        val data = keyValuesRepository.checkValidityAndGetValue(dbKey, RESPONSE_VALIDITY_HOURS)

        when {
            //Local data is not expired
            data.isNotNullOrEmpty() -> {
                Result.Success(DataParser.parseUnsplashData(data, true))
            }

            else -> {
                //Local Data is not available or expired
                if(connectionChecker.isNetworkConnected()) {
                    val params = ApiRequestDataMapper.mapPhotosAndUsersRequest(orderBy, page, perPage, orientation)
                    when(val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS, params)) {
                        is Result.Success -> {
                            //Insert into DB
                            keyValuesRepository.insert(dbKey, result.data)
                            Result.Success(DataParser.parseUnsplashData(result.data, true))
                        }

                        is Result.Failure -> result
                    }
                } else
                    Result.Failure(IllegalStateException(NO_INTERNET))
            }
        }
    }

    suspend fun getSearchedPhotos(searchTerm: String, page: Int, @PerPageRestriction perPage: Int): Result<SearchedPhotos> {
        return getSearchedPhotos(searchTerm, page, perPage, defaultOrientation, RESPONSE_VALIDITY_HOURS)
    }

    suspend fun getSearchedPhotos(searchTerm: String, page: Int, @PerPageRestriction perPage: Int, validityInHours: Int): Result<SearchedPhotos> {
        return getSearchedPhotos(searchTerm, page, perPage, defaultOrientation, validityInHours)
    }

    suspend fun getSearchedPhotos(
        searchTerm: String,
        page: Int,
        @PerPageRestriction perPage: Int,
        @PhotosOrientationRestriction photosOrientation: String,
        validityInHours: Int
    ): Result<SearchedPhotos> = withContext(Dispatchers.IO) {

        val dbKey = SEARCHED_PHOTOS_KEY + searchTerm + page + perPage

        //Check if data available locally and not expired
        val data = keyValuesRepository.checkValidityAndGetValue(dbKey, validityInHours)

        return@withContext when {
            //Local data is not expired
            data.isNotNullOrEmpty() -> {
                Result.Success(DataParser.parseSearchedPhotos(data))
            }

            else -> {
                //Local Data is not available or expired
                if(connectionChecker.isNetworkConnected()) {
                    val params = ApiRequestDataMapper.mapSearchedPhotosRequest(searchTerm, page, perPage, photosOrientation)
                    when(val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_SEARCHED_PHOTOS, params)) {
                        is Result.Success -> {
                            //Insert into DB
                            keyValuesRepository.insert(dbKey, result.data)
                            Result.Success(DataParser.parseSearchedPhotos(result.data))
                        }

                        is Result.Failure -> {
                            result
                        }
                    }
                } else
                    Result.Failure(IllegalStateException(NO_INTERNET))
            }
        }
    }

    suspend fun getCollectionPhotos(collectionId: String): Result<PhotosAndUsers> {
        return getCollectionPhotos(collectionId, 1, 20)
    }

    suspend fun getCollectionPhotos(
        collectionId: String,
        page: Int,
        @PerPageRestriction perPage: Int
    ): Result<PhotosAndUsers> = withContext(Dispatchers.IO) {

        val dbKey = COLLECTION_PHOTOS_KEY + collectionId + page + perPage

        //Check if data available locally and not expired
        val data = keyValuesRepository.checkValidityAndGetValue(dbKey, RESPONSE_VALIDITY_HOURS)

        return@withContext when {
            data.isNotNullOrEmpty() -> {
                //Local data is not expired
                Result.Success(DataParser.parseUnsplashData(data, true))
            }

            else -> {
                //Local Data is not available or expired
                if(connectionChecker.isNetworkConnected()) {
                    val params = ApiRequestDataMapper.mapCollectionPhotosRequest(collectionId, page, perPage)
                    when (val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTION_PHOTOS, params)) {
                        is Result.Success -> {
                            //Insert into DB
                            keyValuesRepository.insert(dbKey, result.data)
                            Result.Success(DataParser.parseUnsplashData(result.data, true))
                        }

                        is Result.Failure -> {
                            result
                        }
                    }
                } else
                    Result.Failure(IllegalStateException(NO_INTERNET))
            }
        }
    }

    suspend fun getCollectionsAndTags(
        page: Int,
        @PerPageRestriction perPage: Int,
    ): Result<CollectionsAndTags> = withContext(Dispatchers.IO) {

        val dbKey = COLLECTION_KEY + page + perPage

        //Check if data available locally and not expired
        val data = keyValuesRepository.checkValidityAndGetValue(dbKey, RESPONSE_VALIDITY_HOURS)

        return@withContext when {

            data.isNotNullOrEmpty() -> {
                //Local data is not expired
                Result.Success(DataParser.parseCollections(data, true))
            }

            else -> {
                //Local Data is not available or expired
                if(connectionChecker.isNetworkConnected()) {
                    val params = ApiRequestDataMapper.mapCollectionsAndTagsRequest(page, perPage)
                    when(val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTIONS, params)) {
                        is Result.Success -> {
                            //Insert into DB
                            keyValuesRepository.insert(dbKey, result.data)
                            Result.Success(DataParser.parseCollections(result.data, true))
                        }

                        is Result.Failure -> {
                            result
                        }
                    }
                } else
                    Result.Failure(IllegalStateException(NO_INTERNET))
            }
        }
    }

    suspend fun getUserPhotos(
        unsplashUserName: String,
        page: Int,
        @PerPageRestriction perPage: Int
    ): Result<List<UnsplashPhotos>> = withContext(Dispatchers.IO) {

        val dbKey = PHOTOGRAPHER_KEY + unsplashUserName + page + perPage

        //Check if data available locally and not expired
        val data = keyValuesRepository.checkValidityAndGetValue(dbKey, RESPONSE_VALIDITY_HOURS)

        return@withContext when {
            data.isNotNullOrEmpty() -> {
                //Local data is not expired
                Result.Success(DataParser.parseUnsplashData(data, true).photosList)
            }

            else -> {
                //Local Data is not available or expired
                if (connectionChecker.isNetworkConnected()) {
                    val params = ApiRequestDataMapper.mapUserPhotosRequest(unsplashUserName, page, perPage)
                    when (val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS_BY_USER, params)) {
                        is Result.Success -> {
                            //Insert into DB
                            keyValuesRepository.insert(dbKey, result.data)
                            Result.Success(DataParser.parseUnsplashData(result.data, true).photosList)
                        }

                        is Result.Failure -> {
                            result
                        }
                    }
                } else {
                    Result.Failure(IllegalStateException(NO_INTERNET))
                }
            }
        }
    }

    suspend fun getUserCollections(
        unsplashUserName: String,
        page: Int,
        @PerPageRestriction perPage: Int
    ): Result<CollectionsAndTags> = withContext(Dispatchers.IO) {

        val dbKey = COLLECTION_KEY + unsplashUserName + page + perPage

        //Check if data available locally and not expired
        val data = keyValuesRepository.checkValidityAndGetValue(dbKey, RESPONSE_VALIDITY_HOURS)

        return@withContext when {
            data.isNotNullOrEmpty() -> {
                //Local data is not expired
                Result.Success(DataParser.parseCollections(data, true))
            }

            else -> {
                //Local Data is not available or expired
                if(connectionChecker.isNetworkConnected()) {

                    val params = ApiRequestDataMapper.mapUserCollectionsRequest(unsplashUserName, page, perPage)

                    when(val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTIONS_BY_USER, params)) {
                        is Result.Success -> {
                            //Insert into DB
                            keyValuesRepository.insert(dbKey, result.data)
                            Result.Success( DataParser.parseCollections(result.data, true))
                        }

                        is Result.Failure -> {
                            result
                        }
                    }
                } else {
                    Result.Failure(IllegalStateException(NO_INTERNET))
                }
            }
        }
    }

    suspend fun getRandomPhoto(
        searchTerm: String = "",
        topicIdsCommaSeparated: String = "",
        count: Int = 1,
        responseExpiryInHours: Int = 0
    ): Result<List<UnsplashPhotos>> = withContext(Dispatchers.IO) {

        val dbKey = RANDOM_PHOTOS_KEY + searchTerm + topicIdsCommaSeparated + count + responseExpiryInHours

        //If expiry date is more than 0 hours then
        // check DB if offline data is available and not expired
        if(responseExpiryInHours > 0) {
            val data = keyValuesRepository.checkValidityAndGetValue(dbKey, responseExpiryInHours)
            data?.takeIf { it.isNotNullOrEmpty() }?.let {
                //Data is available so return from here with Success result
                return@withContext Result.Success(DataParser.parseRandomPhotosData(count, data))
            }
        }

        //Offline data might be not available or
        // data is expired so try getting fresh data from server
        if(connectionChecker.isNetworkConnected()) {
            val params = ApiRequestDataMapper.mapRandomPhotosRequest(searchTerm, topicIdsCommaSeparated, count)
            when(val result = apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_RANDOM_PHOTO, params)) {
                is Result.Success -> {
                    if(responseExpiryInHours > 0) {
                        keyValuesRepository.insert(dbKey, result.data)
                    }
                    Result.Success(DataParser.parseRandomPhotosData(count, result.data))
                }

                is Result.Failure -> {
                    result
                }
            }
        } else {
            Result.Failure(IllegalStateException(NO_INTERNET))
        }
    }

    fun trackDownload(url: String) = scope.launch {
        val extras = HashMap<String, String>()
        extras[DOWNLOADING_PHOTO_URL] = url
        if(connectionChecker.isNetworkConnected())
            apiManager.makeUnsplashRequest(APIManager.SERVICE_POST_DOWNLOADING_PHOTO, extras)
    }

    companion object {

        private const val TAG = "UnsplashHelper"

        private const val PHOTOGRAPHER_KEY = "key_photographer_"
        private const val COLLECTION_KEY = "key_collection_"
        private const val COLLECTION_PHOTOS_KEY = "key_collection_photo_"
        private const val PHOTOS_KEY = "key_photos_"
        private const val SEARCHED_PHOTOS_KEY = "key_searched_photo_"
        private const val RANDOM_PHOTOS_KEY = "key_random_photo_"

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