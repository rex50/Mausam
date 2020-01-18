package com.rex50.mausam.interfaces;

import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos;

import org.json.JSONArray;

public interface GetUnsplashSearchedPhotosListener {
    void onSuccess(SearchedPhotos photos);
    void onFailed(JSONArray errors);
}
