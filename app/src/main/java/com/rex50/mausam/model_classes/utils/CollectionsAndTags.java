package com.rex50.mausam.model_classes.utils;

import com.rex50.mausam.model_classes.unsplash.collection.Collections;
import com.rex50.mausam.model_classes.unsplash.collection.Tag;

import java.util.List;

public class CollectionsAndTags {
    private List<Collections> collectionsList;
    private List<Tag> tagsList;

    private CollectionsAndTags(){}

    public CollectionsAndTags(List<Collections> collectionsList, List<Tag> tagsList){
        this.collectionsList = collectionsList;
        this.tagsList = tagsList;
    }

    public List<Collections> getCollectionsList() {
        return collectionsList;
    }

    public List<Tag> getTagsList() {
        return tagsList;
    }
}
