package com.rex50.mausam.interfaces;

import org.json.JSONArray;

public interface UnsplashJSONArrayListener {
    void onSuccess(JSONArray response);
    void onFailed(JSONArray errorResponse);
}
