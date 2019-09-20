package com.rex50.mausam.Utils;

import com.google.gson.Gson;
import com.rex50.mausam.ModelClasses.WeatherModelClass;

import org.json.JSONObject;

public class DataParser {
    public WeatherModelClass parseWeatherData(JSONObject response){
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(response), WeatherModelClass.class);
    }
}
