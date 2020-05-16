package com.rex50.mausam.utils;

import com.google.gson.Gson;
import com.rex50.mausam.model_classes.unsplash.collection.Collections;
import com.rex50.mausam.model_classes.unsplash.collection.Tag;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos;
import com.rex50.mausam.model_classes.utils.CollectionsAndTags;
import com.rex50.mausam.model_classes.utils.PhotosAndUsers;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataParser {
    public static WeatherModelClass parseWeatherData(JSONObject response){
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(response), WeatherModelClass.class);
    }

    public static PhotosAndUsers parseUnsplashData(String response, boolean parseUsersList){
        Gson gson = new Gson();
        List<UnsplashPhotos> photos = new ArrayList<>();
        List<User> users = new ArrayList<>();
        try {
            JSONArray responseArray = new JSONArray(response);
            if(parseUsersList){
                for (int i = 0; i < responseArray.length(); i++) {
                    UnsplashPhotos obj = gson.fromJson(responseArray.getJSONObject(i).toString(), UnsplashPhotos.class);
                    photos.add(obj);
                    users.add(obj.getUser());
                }
            }else {
                for (int i = 0; i < responseArray.length(); i++) {
                    photos.add(gson.fromJson(responseArray.getJSONObject(i).toString(), UnsplashPhotos.class));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new PhotosAndUsers(photos, users);
    }

    public static SearchedPhotos parseSearchedPhotos(String response){
        Gson gson = new Gson();
        return gson.fromJson(response, SearchedPhotos.class);
    }

    public static CollectionsAndTags parseCollections(String response, boolean parseTagsList){
        Gson gson = new Gson();
        List<Collections> collections = new ArrayList<>();
        Set<Tag> tags = new HashSet<>();
        try {
            JSONArray responseArray = new JSONArray(response);
            if(parseTagsList){
                for (int i = 0; i < responseArray.length(); i++) {
                    Collections obj = gson.fromJson(responseArray.getJSONObject(i).toString(), Collections.class);
                    collections.add(obj);
                    tags.addAll(obj.getTags());
                }
            }else {
                for (int i = 0; i < responseArray.length(); i++) {
                    collections.add(gson.fromJson(responseArray.getJSONObject(i).toString(), Collections.class));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new CollectionsAndTags(collections, new ArrayList<>(tags));
    }
}
