package com.rex50.mausam.model_classes.unsplash.searched_photos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;

import java.util.List;

public class SearchedPhotos {
    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("total_pages")
    @Expose
    private int total_pages;

    @SerializedName("results")
    @Expose
    private List<UnsplashPhotos> results;

    public List<UnsplashPhotos> getResults() {
        return results;
    }

    public void setResults(List<UnsplashPhotos> results) {
        this.results = results;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int totalPages) {
        this.total_pages = totalPages;
    }
}
