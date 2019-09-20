package com.rex50.mausam.Network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Utils.DataParser;
import com.rex50.mausam.Utils.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class APIManager {

    private static String baseUrl = "https://api.openweathermap.org/";

    private static String appId = "d45cbb97323891724a62b9c87aeffbe8";

    private static volatile APIManager apiManager;

    public static int SERVICE_CURRENT_WEATHER = 1;

    private volatile HashMap<Integer, String> serviceTable;

    private Context ctx;

    private static Object mutex = new Object();
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
        String url;
        mgr.serviceTable = new HashMap<>(0);
        mgr.serviceTable.put(SERVICE_CURRENT_WEATHER, "data/2.5/weather");
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

    public void getCurrentWeather(int service, HashMap<String, String> urlExtras, final CallBackResponse listener){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateUrl(service, urlExtras), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DataParser dataParser = new DataParser();
                        listener.onWeatherResponseSuccess(dataParser.parseWeatherData(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onWeatherResponseFailure(error.toString());
                    }
                });

        VolleySingleton volleySingleton = VolleySingleton.getInstance(ctx);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    public interface CallBackResponse{
        void onWeatherResponseSuccess(WeatherModelClass weatherDetails);
        void onWeatherResponseFailure(String msg);
    }

}
