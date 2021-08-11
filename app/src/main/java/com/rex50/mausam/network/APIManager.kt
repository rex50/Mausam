package com.rex50.mausam.network

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.IntDef
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.rex50.mausam.BuildConfig
import com.rex50.mausam.R
import com.rex50.mausam.utils.Constants.ApiConstants.COLLECTION_ID
import com.rex50.mausam.utils.Constants.ApiConstants.DOWNLOADING_PHOTO_URL
import com.rex50.mausam.utils.Constants.ApiConstants.UNSPLASH_USERNAME
import com.rex50.mausam.utils.Utils
import com.rex50.mausam.utils.VolleySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.coroutines.Continuation

class APIManager private constructor(private val ctx: Context?) {
    var properties: Properties? = null

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(SERVICE_CURRENT_WEATHER, SERVICE_SEARCH_WEATHER_BY_PLACE)
    private annotation class WeatherApiService

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        SERVICE_GET_PHOTOS,
        SERVICE_GET_PHOTOS_BY_ID,
        SERVICE_GET_RANDOM_PHOTO,
        SERVICE_GET_COLLECTION_PHOTOS,
        SERVICE_GET_COLLECTIONS,
        SERVICE_GET_FEATURED_COLLECTIONS,
        SERVICE_GET_USER_PUBLIC_PROFILE,
        SERVICE_GET_PHOTOS_BY_USER,
        SERVICE_GET_SEARCHED_PHOTOS,
        SERVICE_GET_COLLECTIONS_BY_USER,
        SERVICE_POST_DOWNLOADING_PHOTO
    )
    private annotation class UnsplashApiService

    @Volatile
    private var serviceTable: HashMap<Int, String> = hashMapOf()
    private fun generateUrl(
        baseUrl: String,
        service: Int,
        urlExtras: HashMap<String, String>
    ): String {
        var uri = Uri.parse(baseUrl).buildUpon()
        when (service) {
            SERVICE_GET_PHOTOS_BY_USER, SERVICE_GET_COLLECTIONS_BY_USER -> {
                require(urlExtras.containsKey(UNSPLASH_USERNAME)) {
                    "For $service, $UNSPLASH_USERNAME is required"
                }

                uri.path(
                    String.format(serviceTable[service]!!, urlExtras[UNSPLASH_USERNAME]
                    )
                )

                urlExtras.remove(UNSPLASH_USERNAME)
            }
            SERVICE_GET_COLLECTION_PHOTOS -> {
                require(urlExtras.containsKey(COLLECTION_ID)) {
                    "For $service, $COLLECTION_ID is required"
                }

                uri.path(
                    String.format(
                        serviceTable[service]!!, urlExtras[COLLECTION_ID]
                    )
                )

                urlExtras.remove(COLLECTION_ID)
            }
            SERVICE_POST_DOWNLOADING_PHOTO -> {
                require(urlExtras.containsKey(DOWNLOADING_PHOTO_URL)) {
                    "For $service, $DOWNLOADING_PHOTO_URL is required"
                }
                uri = Uri.parse(urlExtras[DOWNLOADING_PHOTO_URL]).buildUpon()
                urlExtras.remove(DOWNLOADING_PHOTO_URL)
            }
            else -> uri.path(serviceTable[service])
        }
        for ((key, value) in urlExtras) {
            uri.appendQueryParameter(key, value)
        }
        return uri.build().toString()
    }

    fun getWeather(
        @WeatherApiService service: Int,
        urlExtras: HashMap<String, String>,
        listener: WeatherAPICallBackResponse
    ) {
        urlExtras["appid"] = appId ?: ""
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            generateUrl(baseUrlWeather, service, urlExtras),
            null,
            { weatherDetails: JSONObject? ->
                weatherDetails?.let {
                    listener.onWeatherResponseSuccess(weatherDetails)
                } ?: listener.onWeatherResponseFailure(
                    Utils.WEATHER_NOT_FOUND,
                    "Sorry something went wrong try again later."
                )
            },
            {
                listener.onWeatherResponseFailure(
                    Utils.WEATHER_NOT_FOUND,
                    "Sorry something went wrong try again later."
                )
            }
        )
        val volleySingleton: VolleySingleton = VolleySingleton.getInstance(ctx)
        volleySingleton.addToRequestQueue(jsonObjectRequest)
    }

    fun searchWeather(
        @WeatherApiService service: Int,
        urlExtras: HashMap<String, String>,
        listener: WeatherAPICallBackResponse
    ) {
        urlExtras["appid"] = appId ?: ""
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, generateUrl(baseUrlWeather, service, urlExtras), null,
            { response: JSONObject ->
                if (response.optString("cod") == "404") {
                    listener.onWeatherResponseFailure(
                        Utils.PAGE_NOT_FOUND,
                        response.optString("message")
                    )
                } else {
                    listener.onWeatherResponseSuccess(response)
                }
            },
            {
                listener.onWeatherResponseFailure(
                    Utils.CITY_NOT_FOUND,
                    "City not found"
                )
            })
        val volleySingleton: VolleySingleton = VolleySingleton.getInstance(ctx)
        volleySingleton.addToRequestQueue(jsonObjectRequest)
    }

    suspend fun makeUnsplashRequest(
        @UnsplashApiService service: Int,
        urlExtras: HashMap<String, String>
    ): Result<String> {
        return try {
            suspendCancellableCoroutine { continuation ->
                makeUnsplashRequest(service, continuation, urlExtras)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun makeFeedbackRequest(
        msg: String
    ): Result<String> = withContext(Dispatchers.IO){
        return@withContext try {
            suspendCancellableCoroutine { continuation ->
                makeFeedbackRequest(msg, continuation)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    private fun makeFeedbackRequest(
        msg: String,
        continuation: Continuation<Result<String>>
    ) {

        val request = object: StringRequest(
            Method.POST,
            feedbackUrl,
            Response.Listener { response ->
                continuation.resumeWith(kotlin.Result.success(Result.Success(response)))
            },
            Response.ErrorListener { error ->
                continuation.resumeWith(kotlin.Result.failure(error))
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf<String, String>().also {
                    it[feedbackColumn!!] = msg
                }
            }
        }

        val volley = VolleySingleton.getInstance(ctx)
        volley.addToRequestQueue(request)

    }

    private fun makeUnsplashRequest(
        @UnsplashApiService service: Int,
        continuation: Continuation<Result<String>>,
        urlExtras: HashMap<String, String>
    ) {
        urlExtras["client_id"] = ctx?.getString(R.string.unsplashAccessKey) ?: ""

        val url = generateUrl(
            baseUrlUnsplash,
            service,
            urlExtras
        )

        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            url,
            Response.Listener { response: String ->
                if (BuildConfig.DEBUG) Log.d("Volley", "makeUnsplashRequest: $response")
                if (!response.contains("\"errors\":")) {
                    continuation.resumeWith(kotlin.Result.success(Result.Success(response)))
                } else {
                    try {
                        val obj = JSONObject(response)
                        continuation.resumeWith(kotlin.Result.failure(Exception(obj.optJSONArray("errors")?.toString() ?: "[]")))
                    } catch (e: JSONException) {
                        continuation.resumeWith(kotlin.Result.failure(e))
                    }
                }
            },
            Response.ErrorListener { error: VolleyError ->
                if (BuildConfig.DEBUG) Log.e("Volley", "makeUnsplashRequest: $error")
                continuation.resumeWith(kotlin.Result.failure(error))
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf<String, String>().also {
                    it["client_id"] = ctx?.getString(R.string.unsplashAccessKey) ?: ""
                }
            }

            override fun getBody(): ByteArray? {
                //return "Your JSON body".toString().getBytes();
                return null
            }
        }
        val volleySingleton: VolleySingleton = VolleySingleton.getInstance(ctx)
        volleySingleton.addToRequestQueue(stringRequest)
    }

    interface WeatherAPICallBackResponse {
        fun onWeatherResponseSuccess(weatherDetails: JSONObject)
        fun onWeatherResponseFailure(errorCode: Int, msg: String)
    }

    companion object {

        const val TAG = "APIManager"

        private const val baseUrlWeather = "https://api.openweathermap.org/"
        private const val baseUrlUnsplash = "https://api.unsplash.com/"
        private var appId: String? = null

        //Feedback related code
        private var feedbackUrl: String? = null
        private var feedbackColumn: String? = null

        @Volatile
        private var apiManager: APIManager? = null
        const val SERVICE_CURRENT_WEATHER = 1
        const val SERVICE_SEARCH_WEATHER_BY_PLACE = 2
        const val SERVICE_GET_PHOTOS = 3 //JSONArray response
        const val SERVICE_GET_PHOTOS_BY_ID = 4 //JSONObject response
        const val SERVICE_GET_RANDOM_PHOTO = 5 //JSONObject response
        const val SERVICE_GET_COLLECTIONS = 6 //JSONArray response
        const val SERVICE_GET_COLLECTION_PHOTOS = 7 //JSONArray response
        const val SERVICE_GET_FEATURED_COLLECTIONS = 8 //JSONArray response
        const val SERVICE_GET_USER_PUBLIC_PROFILE = 9 //JSONObject response
        const val SERVICE_GET_PHOTOS_BY_USER = 10 //JSONArray response
        const val SERVICE_GET_SEARCHED_PHOTOS = 11 //JSONArray response
        const val SERVICE_GET_COLLECTIONS_BY_USER = 12 //JSONObject response
        const val SERVICE_POST_DOWNLOADING_PHOTO = 13 //JSONObject response

        /*
         *************************************************  Weather Urls *************************************************
         */
        private const val URL_GET_WEATHER = "data/2.5/weather"


        /*
         ************************************************* Unsplash Urls *************************************************
         */
        /**
         * Photo Urls
         */
        private const val URL_GET_PHOTOS = "photos"
        private const val URL_GET_PHOTO_BY_ID = "photos/%s"
        private const val URL_GET_SEARCHED_PHOTOS = "search/photos"
        private const val URL_GET_RANDOM_PHOTOS = "photos/random"

        /**
         * Collections Urls
         */
        private const val URL_GET_COLLECTIONS = "collections"
        private const val URl_GET_COLLECTION_PHOTOS = "collections/%s/photos"
        private const val URL_GET_FEATURED_COLLECTIONS = "collections/featured"

        /**
         * User related Urls
         */
        private const val URL_GET_USER_PUBLIC_PROFILE = "users/%s"
        private const val URL_GET_PHOTOS_BY_USER = "users/%s/photos"
        private const val URL_GET_COLLECTIONS_BY_USER = "users/%s/collections"

        private val mutex = Any()
        @JvmStatic
        fun getInstance(ctx: Context?): APIManager? {
            var instance = apiManager
            if (instance == null) {
                synchronized(mutex) {
                    instance = apiManager
                    if (instance == null) {
                        instance = APIManager(ctx)
                        apiManager = instance
                        appId = ctx?.getString(R.string.openWeatherAppId)
                        feedbackUrl = "https://docs.google.com/forms/u/0/d/e/${ctx?.getString(R.string.feedbackFormId)}/formResponse"
                        feedbackColumn = "entry.1187937887"
                        setupServiceTable(apiManager)
                    }
                }
            }
            return instance
        }

        private fun setupServiceTable(mgr: APIManager?) {
            mgr?.apply {
                serviceTable = HashMap(0)

                //Weather Urls
                serviceTable[SERVICE_CURRENT_WEATHER] = URL_GET_WEATHER
                serviceTable[SERVICE_SEARCH_WEATHER_BY_PLACE] =
                    URL_GET_WEATHER

                //Unsplash Urls
                serviceTable[SERVICE_GET_PHOTOS] = URL_GET_PHOTOS
                serviceTable[SERVICE_GET_PHOTOS_BY_ID] = URL_GET_PHOTO_BY_ID
                serviceTable[SERVICE_GET_RANDOM_PHOTO] = URL_GET_RANDOM_PHOTOS
                serviceTable[SERVICE_GET_COLLECTIONS] = URL_GET_COLLECTIONS
                serviceTable[SERVICE_GET_COLLECTION_PHOTOS] = URl_GET_COLLECTION_PHOTOS
                serviceTable[SERVICE_GET_FEATURED_COLLECTIONS] = URL_GET_FEATURED_COLLECTIONS
                serviceTable[SERVICE_GET_USER_PUBLIC_PROFILE] = URL_GET_USER_PUBLIC_PROFILE
                serviceTable[SERVICE_GET_PHOTOS_BY_USER] = URL_GET_PHOTOS_BY_USER
                serviceTable[SERVICE_GET_SEARCHED_PHOTOS] = URL_GET_SEARCHED_PHOTOS
                serviceTable[SERVICE_GET_COLLECTIONS_BY_USER] = URL_GET_COLLECTIONS_BY_USER
                serviceTable[SERVICE_POST_DOWNLOADING_PHOTO] = ""
            }
        }
    }
}