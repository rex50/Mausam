package com.rex50.mausam.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

public interface UnsplashJSONObjectListener {
    void onSuccess(JSONObject response);
    void onFailed(JSONArray errorResponse);
}
