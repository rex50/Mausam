package com.rex50.mausam.model_classes.utils;

import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;

import java.util.List;

public class PhotosAndUsers {
    private List<UnsplashPhotos> photosList;
    private List<User> userList;

    private PhotosAndUsers(){}

    public PhotosAndUsers(List<UnsplashPhotos> photosList, List<User> userList){
        this.photosList = photosList;
        this.userList = userList;
    }

    public List<UnsplashPhotos> getPhotosList() {
        return photosList;
    }

    public List<User> getUserList() {
        return userList;
    }
}
