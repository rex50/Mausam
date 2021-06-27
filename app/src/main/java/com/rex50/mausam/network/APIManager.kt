package com.rex50.mausam.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.IntDef;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.rex50.mausam.BuildConfig;
import com.rex50.mausam.R;
import com.rex50.mausam.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static com.rex50.mausam.utils.Constants.ApiConstants.COLLECTION_ID;
import static com.rex50.mausam.utils.Constants.ApiConstants.DOWNLOADING_PHOTO_URL;
import static com.rex50.mausam.utils.Constants.ApiConstants.UNSPLASH_USERNAME;
import static com.rex50.mausam.utils.Utils.CITY_NOT_FOUND;
import static com.rex50.mausam.utils.Utils.PAGE_NOT_FOUND;
import static com.rex50.mausam.utils.Utils.WEATHER_NOT_FOUND;

public final class APIManager {

    private static String baseUrlWeather = "https://api.openweathermap.org/";
    private static String baseUrlUnsplash = "https://api.unsplash.com/";

    private static String appId;
    Properties properties;

    private static volatile APIManager apiManager;

    public static final int SERVICE_CURRENT_WEATHER = 1;
    public static final int SERVICE_SEARCH_WEATHER_BY_PLACE = 2;

    public static final int SERVICE_GET_PHOTOS = 3; //JSONArray response
    public static final int SERVICE_GET_PHOTOS_BY_ID = 4; //JSONObject response
    public static final int SERVICE_GET_RANDOM_PHOTO = 5; //JSONObject response
    public static final int SERVICE_GET_COLLECTIONS = 6; //JSONArray response
    public static final int SERVICE_GET_COLLECTION_PHOTOS = 7; //JSONArray response
    public static final int SERVICE_GET_FEATURED_COLLECTIONS = 8; //JSONArray response
    public static final int SERVICE_GET_USER_PUBLIC_PROFILE = 9; //JSONObject response
    public static final int SERVICE_GET_PHOTOS_BY_USER = 10; //JSONArray response
    public static final int SERVICE_GET_SEARCHED_PHOTOS = 11; //JSONArray response
    public static final int SERVICE_GET_COLLECTIONS_BY_USER = 12; //JSONObject response
    public static final int SERVICE_POST_DOWNLOADING_PHOTO = 13; //JSONObject response

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVICE_CURRENT_WEATHER, SERVICE_SEARCH_WEATHER_BY_PLACE})
    private @interface WeatherApiService {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVICE_GET_PHOTOS, SERVICE_GET_PHOTOS_BY_ID, SERVICE_GET_RANDOM_PHOTO, SERVICE_GET_COLLECTION_PHOTOS,
            SERVICE_GET_COLLECTIONS, SERVICE_GET_FEATURED_COLLECTIONS, SERVICE_GET_USER_PUBLIC_PROFILE,
            SERVICE_GET_PHOTOS_BY_USER, SERVICE_GET_SEARCHED_PHOTOS, SERVICE_GET_COLLECTIONS_BY_USER,
            SERVICE_POST_DOWNLOADING_PHOTO})
    private @interface UnsplashApiService {
    }

    /*
     *************************************************  Weather Urls *************************************************
     */
    private static final String URL_GET_WEATHER =
            "data/2.5/weather";


    /*
     ************************************************* Unsplash Urls *************************************************
     */

    /**
     * Photo Urls
     */
    private static final String URL_GET_PHOTOS =
            "photos";

    private static final String URL_GET_PHOTO_BY_ID =
            "photos/%s";

    private static final String URL_GET_SEARCHED_PHOTOS =
            "search/photos";

    private static final String URL_GET_RANDOM_PHOTOS =
            "photos/random";


    /**
     * Collections Urls
     */
    private static final String URL_GET_COLLECTIONS =
            "collections";

    private static final String URl_GET_COLLECTION_PHOTOS =
            "collections/%s/photos";

    private static final String URL_GET_FEATURED_COLLECTIONS =
            "collections/featured";


    /**
     * User related Urls
     */
    private static final String URL_GET_USER_PUBLIC_PROFILE =
            "users/%s";

    private static final String URL_GET_PHOTOS_BY_USER =
            "users/%s/photos";

    private static final String URL_GET_COLLECTIONS_BY_USER =
            "users/%s/collections";



    private volatile HashMap<Integer, String> serviceTable;

    private Context ctx;

    private static final Object mutex = new Object();
    public static APIManager getInstance(Context ctx){
        APIManager instance = apiManager;
        if (instance == null) {
            synchronized (mutex) {
                instance = apiManager;
                if (instance == null) {
                    apiManager = instance = new APIManager(ctx);
                    appId = ctx.getString(R.string.openWeatherAppId);
                    setupServiceTable(apiManager);
                }
            }
        }
        return instance;
    }

    private APIManager(Context ctx){
        this.ctx = ctx;
    }

    private static void setupServiceTable(APIManager mgr){
        mgr.serviceTable = new HashMap<>(0);

        //Weather Urls
        mgr.serviceTable.put(SERVICE_CURRENT_WEATHER, URL_GET_WEATHER);
        mgr.serviceTable.put(SERVICE_SEARCH_WEATHER_BY_PLACE, URL_GET_WEATHER);

        //Unsplash Urls
        mgr.serviceTable.put(SERVICE_GET_PHOTOS, URL_GET_PHOTOS);
        mgr.serviceTable.put(SERVICE_GET_PHOTOS_BY_ID, URL_GET_PHOTO_BY_ID);
        mgr.serviceTable.put(SERVICE_GET_RANDOM_PHOTO, URL_GET_RANDOM_PHOTOS);
        mgr.serviceTable.put(SERVICE_GET_COLLECTIONS, URL_GET_COLLECTIONS);
        mgr.serviceTable.put(SERVICE_GET_COLLECTION_PHOTOS, URl_GET_COLLECTION_PHOTOS);
        mgr.serviceTable.put(SERVICE_GET_FEATURED_COLLECTIONS, URL_GET_FEATURED_COLLECTIONS);
        mgr.serviceTable.put(SERVICE_GET_USER_PUBLIC_PROFILE, URL_GET_USER_PUBLIC_PROFILE);
        mgr.serviceTable.put(SERVICE_GET_PHOTOS_BY_USER, URL_GET_PHOTOS_BY_USER);
        mgr.serviceTable.put(SERVICE_GET_SEARCHED_PHOTOS, URL_GET_SEARCHED_PHOTOS);
        mgr.serviceTable.put(SERVICE_GET_COLLECTIONS_BY_USER, URL_GET_COLLECTIONS_BY_USER);
        mgr.serviceTable.put(SERVICE_POST_DOWNLOADING_PHOTO, "");
    }

    private String generateUrl(String baseUrl,int service, HashMap<String, String> urlExtras) {
        Uri.Builder uri = Uri.parse(baseUrl).buildUpon();
        switch (service){
            case SERVICE_GET_PHOTOS_BY_USER:
            case SERVICE_GET_COLLECTIONS_BY_USER:
                if(!urlExtras.containsKey(UNSPLASH_USERNAME))
                    throw new IllegalArgumentException("For " + service + ", " + UNSPLASH_USERNAME + " is required");
                uri.path(String.format(Objects.requireNonNull(serviceTable.get(service)), urlExtras.get(UNSPLASH_USERNAME)));
                urlExtras.remove(UNSPLASH_USERNAME);
                break;
            case SERVICE_GET_COLLECTION_PHOTOS:
                if(!urlExtras.containsKey(COLLECTION_ID))
                    throw new IllegalArgumentException("For " + service + ", " + COLLECTION_ID + " is required");
                uri.path(String.format(Objects.requireNonNull(serviceTable.get(service)), urlExtras.get(COLLECTION_ID)));
                urlExtras.remove(COLLECTION_ID);
                break;
            case SERVICE_POST_DOWNLOADING_PHOTO:
                if(!urlExtras.containsKey(DOWNLOADING_PHOTO_URL))
                    throw new IllegalArgumentException("For " + service + ", " + DOWNLOADING_PHOTO_URL + " is required");
                uri = Uri.parse(urlExtras.get(DOWNLOADING_PHOTO_URL)).buildUpon();
                urlExtras.remove(DOWNLOADING_PHOTO_URL);
                break;
            default:
                uri.path(serviceTable.get(service));

        }
        if (urlExtras != null) {
            for (Map.Entry parameter : urlExtras.entrySet()) {
                uri.appendQueryParameter(parameter.getKey().toString(), parameter.getValue().toString());
            }
        }
        return uri.build().toString();
    }

    public void getWeather(@WeatherApiService int service, HashMap<String, String> urlExtras, final WeatherAPICallBackResponse listener){
        urlExtras.put("appid", appId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(baseUrlWeather, service, urlExtras), null,
                listener::onWeatherResponseSuccess,
                error -> listener.onWeatherResponseFailure(WEATHER_NOT_FOUND, "Sorry something went wrong try again later.")
        );

        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    public void searchWeather(@WeatherApiService int service, HashMap<String, String> urlExtras, final WeatherAPICallBackResponse listener){
        urlExtras.put("appid", appId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(baseUrlWeather, service, urlExtras), null,
                response -> {
                    if(response.optString("cod").equals("404")){
                        listener.onWeatherResponseFailure(PAGE_NOT_FOUND, response.optString("message"));
                    }else {
                        listener.onWeatherResponseSuccess(response);
                    }
                },
                error -> listener.onWeatherResponseFailure(CITY_NOT_FOUND, "City not found"));
        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }


    protected void makeUnsplashRequest(@UnsplashApiService int service, HashMap<String, String> urlExtras, UnsplashAPICallResponse listener){
        makeUnsplashRequest(service, urlExtras, Request.Method.GET, listener);
    }
    protected void makeUnsplashRequest(@UnsplashApiService int service, HashMap<String, String> urlExtras, int method, UnsplashAPICallResponse listener){
        urlExtras.put("client_id", ctx.getString(R.string.unsplashAccessKey));
        StringRequest stringRequest = new StringRequest(method, generateUrl(baseUrlUnsplash, service, urlExtras),
                response -> {
                    if(BuildConfig.DEBUG)
                        Log.d("Volley", "makeUnsplashRequest: " + response);
                    if(!response.contains("\"errors\":")){
                        if(listener != null)
                            listener.onSuccess(response);
                    }else {
                        try {
                            JSONObject object = new JSONObject(response);
                            if(listener != null)
                                listener.onFailed(object.optJSONArray("errors"));
                        } catch (JSONException e) {
                            if(listener != null)
                                listener.onFailed(new JSONArray());
                        }
                    }
                }, error -> {
                    if(BuildConfig.DEBUG)
                        Log.e("Volley", "makeUnsplashRequest: " + error);
                    if(listener != null)
                        listener.onFailed(new JSONArray());
                }){
            @Override
            public Map<String, String> getHeaders(){
                /*HashMap<String, String> httpAuthentication = new HashMap<>();
                httpAuthentication.put("client_id", ctx.getString(R.string.unsplashAccessKey));
                return httpAuthentication*/;
                return new HashMap<>();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
//                return "Your JSON body".toString().getBytes();
                return null;
            }
        };

        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(stringRequest);
    }

    public interface WeatherAPICallBackResponse {
        void onWeatherResponseSuccess(JSONObject weatherDetails);
        void onWeatherResponseFailure(int errorCode, String msg);
    }

    public interface UnsplashAPICallResponse{
        void onSuccess(String response);
        void onFailed(JSONArray errors);
    }

}
