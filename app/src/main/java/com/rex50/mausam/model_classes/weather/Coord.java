package com.rex50.mausam.model_classes.weather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Coord implements Serializable {

    @SerializedName("lon")
    private Double lon;

    @SerializedName("lat")
    private Double lat;

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

}