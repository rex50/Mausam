package com.rex50.mausam.interfaces;

import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;

import org.json.JSONArray;

import java.util.List;

public interface GetUnsplashPhotosListener {
    void onSuccess(List<UnsplashPhotos> photos);
    void onFailed(JSONArray errors);
}
