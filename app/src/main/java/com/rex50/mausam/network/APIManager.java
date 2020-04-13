package com.rex50.mausam.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.IntDef;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.rex50.mausam.R;
import com.rex50.mausam.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.rex50.mausam.utils.Utils.*;

public final class APIManager {

    private static String baseUrlWeather = "https://api.openweathermap.org/";
    private static String baseUrlUnsplash = "https://api.unsplash.com/";

    private static String appId;
    private static String access_key;
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
    public static final int SERVICE_GET_SEARCHED_PHOTOS = 11; //JSONObject response

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVICE_CURRENT_WEATHER, SERVICE_SEARCH_WEATHER_BY_PLACE})
    private @interface WeatherApiService {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVICE_GET_PHOTOS, SERVICE_GET_PHOTOS_BY_ID, SERVICE_GET_RANDOM_PHOTO, SERVICE_GET_COLLECTION_PHOTOS,
            SERVICE_GET_COLLECTIONS, SERVICE_GET_FEATURED_COLLECTIONS, SERVICE_GET_USER_PUBLIC_PROFILE,
            SERVICE_GET_PHOTOS_BY_USER, SERVICE_GET_SEARCHED_PHOTOS})
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
     * Collection Urls
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
//                    access_key = ctx.getString(R.string.unsplashAccessKey);
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
    }

    private String generateUrl(String baseUrl,int service, HashMap<String, String> urlExtras) {
        Uri.Builder uri = Uri.parse(baseUrl).buildUpon();
        uri.path(serviceTable.get(service));
        if (urlExtras != null) {
            for (Map.Entry parameter : urlExtras.entrySet()) {
                uri.appendQueryParameter(parameter.getKey().toString(), parameter.getValue().toString());
            }
        }
        uri.appendQueryParameter("appid",appId);
        Log.d("Response", uri.build().toString());
        return uri.build().toString();
    }

    public void getWeather(@WeatherApiService int service, HashMap<String, String> urlExtras, final WeatherAPICallBackResponse listener){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(baseUrlWeather, service, urlExtras), null,
                listener::onWeatherResponseSuccess,
                error -> listener.onWeatherResponseFailure(WEATHER_NOT_FOUND, "Sorry something went wrong try again later.")
        );

        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    public void searchWeather(@WeatherApiService int service, HashMap<String, String> urlExtras, final WeatherAPICallBackResponse listener){
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

    protected void makeUnsplashRequest(@UnsplashApiService int service, HashMap<String, String> urlExtras, UnsplashAPICallResponse listner){
        urlExtras.put("client_id", ctx.getString(R.string.unsplashAccessKey));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, generateUrl(baseUrlUnsplash, service, urlExtras),
                response -> {
                    if(!response.contains("\"errors\":")){
                        listner.onSuccess(response);
                    }else {
                        try {
                            JSONObject object = new JSONObject(response);
                            listner.onFailed(object.optJSONArray("errors"));
                        } catch (JSONException e) {
                            listner.onFailed(new JSONArray());
                        }
                    }
                }, error -> {
                    listner.onFailed(new JSONArray());
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