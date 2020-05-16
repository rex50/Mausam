package com.rex50.mausam.interfaces;

import com.rex50.mausam.model_classes.unsplash.collection.Collections;
import com.rex50.mausam.model_classes.unsplash.collection.Tag;

import org.json.JSONArray;

import java.util.List;

public interface GetUnsplashCollectionsAndTagsListener {
    void onSuccess(List<Collections> collection, List<Tag> tagsList);
    void onFailed(JSONArray errors);
}
