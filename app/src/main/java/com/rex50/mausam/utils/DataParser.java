package com.rex50.mausam.utils;

import com.google.gson.Gson;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataParser {
    public WeatherModelClass parseWeatherData(JSONObject response){
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(response), WeatherModelClass.class);
    }

    public List<UnsplashPhotos> parseUnsplashData(String response){
        Gson gson = new Gson();
        List<UnsplashPhotos> list = new ArrayList<>();
        try {
            JSONArray responseArray = new JSONArray(response);
            for (int i = 0; i < response.length(); i++) {
                list.add(gson.fromJson(responseArray.getJSONObject(i).toString(), UnsplashPhotos.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public SearchedPhotos parseSearchedPhotos(String response){
        Gson gson = new Gson();
        return gson.fromJson(response, SearchedPhotos.class);
    }
}
