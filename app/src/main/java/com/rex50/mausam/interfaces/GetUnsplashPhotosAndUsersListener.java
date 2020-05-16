package com.rex50.mausam.interfaces;

import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;

import org.json.JSONArray;

import java.util.List;

public interface GetUnsplashPhotosAndUsersListener {
    void onSuccess(List<UnsplashPhotos> photos, List<User> usersList);
    void onFailed(JSONArray errors);
}
