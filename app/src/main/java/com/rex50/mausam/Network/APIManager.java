package com.rex50.mausam.Network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.IntDef;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Utils.DataParser;
import com.rex50.mausam.Utils.VolleySingleton;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

public final class APIManager {

    private static String baseUrl = "https://api.openweathermap.org/";

    private static String appId = "d45cbb97323891724a62b9c87aeffbe8";

    private static volatile APIManager apiManager;

    public static final int SERVICE_CURRENT_WEATHER = 1;
    public static final int SERVICE_SEARCH_WEATHER_BY_PLACE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVICE_CURRENT_WEATHER, SERVICE_SEARCH_WEATHER_BY_PLACE})
    private @interface ApiService{
    }


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
//        String url;
        mgr.serviceTable = new HashMap<>(0);
        mgr.serviceTable.put(SERVICE_CURRENT_WEATHER, "data/2.5/weather");
        mgr.serviceTable.put(SERVICE_SEARCH_WEATHER_BY_PLACE, "data/2.5/weather");
    }

    private String generateUrl(int service, HashMap<String, String> urlExtras) {
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

    public void getCurrentWeather(@ApiService int service, HashMap<String, String> urlExtras, final CallBackResponse listener){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(service, urlExtras), null,
                response -> {
                    DataParser dataParser = new DataParser();
                    listener.onWeatherResponseSuccess(dataParser.parseWeatherData(response));
                },
                error -> listener.onWeatherResponseFailure("Sorry something went wrong try again later."));

        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    public void searchWeather(@ApiService int service, HashMap<String, String> urlExtras, final CallBackResponse listener){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(service, urlExtras), null,
                response -> {
                    if(response.optString("cod").equals("404")){
                        listener.onWeatherResponseFailure(response.optString("message"));
                    }else {
                        DataParser dataParser = new DataParser();
                        listener.onWeatherResponseSuccess(dataParser.parseWeatherData(response));
                    }
                },
                error -> listener.onWeatherResponseFailure("City not found"));
        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    public interface CallBackResponse{
        void onWeatherResponseSuccess(WeatherModelClass weatherDetails);
        void onWeatherResponseFailure(String msg);
    }

}
