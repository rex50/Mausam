package com.rex50.mausam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class MausamSharedPrefs {

    private static final String SHARED_PREFS_NAME = "MausamSharedPrefs";
    private static final String IS_FIRST_TIME = "isFirstTime";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String IS_LOCATION_PERMANENTLY_DENIED = "isLocationPermissionDeniedPermanently";
    private static final String IS_LOCATION_PERMISSION_SKIPPED = "isLocationPermissionSkipped";
    private static final String IS_STORAGE_PERMANENTLY_DENIED = "isStoragePermissionDeniedPermanently";
    private static final String IS_STORAGE_PERMISSION_SKIPPED = "isStoragePermissionSkipped";
    private static final String LAST_WEATHER_DATA = "lastWeatherData";
    private static final String USER_LOCATION = "userLocation";
    private static final String LAST_LOCATION_DETAILS = "locationDetails";
    private static final String LAST_WEATHER_UPDATED = "lastWeatherUpdated";
    private static final String PHOTOS_RESPONSE = "photosResponse";

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private int PRIVATE_MODE = 0;

    public MausamSharedPrefs(Context context){
        this.context = context;
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, PRIVATE_MODE);
        editor = sharedPrefs.edit();
    }

    public void setIsFirstTime(Boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public Boolean getIsFirstTime(){
        return sharedPrefs.getBoolean(IS_FIRST_TIME, true);
    }

    public void setLatitude(Double latitude){
        editor.putLong(LATITUDE, Double.doubleToRawLongBits(latitude));
        editor.commit();
    }

    public Double getLatitude(){
        return Double.longBitsToDouble(
                sharedPrefs.getLong(LATITUDE, Double.doubleToLongBits(0))
        );
    }

    public void setLongitude(Double longitude){
        editor.putLong(LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.commit();
    }

    public Double getLongitude(){
        return Double.longBitsToDouble(
                sharedPrefs.getLong(LONGITUDE, Double.doubleToLongBits(0))
        );
    }

    public void setLastWeatherUpdated(DateTime dateTime){
        editor.putString(LAST_WEATHER_UPDATED, dateTime.toString());
        editor.commit();
    }

    public DateTime getLastWeatherUpdated(){
        return sharedPrefs.getString(LAST_WEATHER_UPDATED, "null").equalsIgnoreCase("null") ?
                null : new DateTime(sharedPrefs.getString(LAST_WEATHER_UPDATED, "null"));
    }

    public void setUserLocation(String location){
        editor.putString(USER_LOCATION, location);
        editor.commit();
    }

    public String getUserLocation(){
        return sharedPrefs.getString(USER_LOCATION, "null");
    }

    public void setPhotosResponse(String response){
        editor.putString(PHOTOS_RESPONSE, response);
        editor.commit();
    }

    public String getPhotosResponse(){
        return sharedPrefs.getString(PHOTOS_RESPONSE, "");
    }

    public void setPhotosResponseMap(String key, String response){
        editor.putString(key, response);
        editor.commit();
    }

    public String getFromPhotosResponseMap(String key){
        return sharedPrefs.getString(key, "");
    }

    public void setLastLocationDetails(JSONObject jsonObject){
        editor.putString(LAST_LOCATION_DETAILS, jsonObject.toString());
        editor.commit();
    }

    public JSONObject getLastLocationDetails(){
        try {
            String data = sharedPrefs.getString(LAST_LOCATION_DETAILS, "null");
            if(!("null").equals(data))
                return new JSONObject(data);
            else
                return null;
        } catch (JSONException e) {
            return null;
        }
    }

    public void setLastWeatherData(JSONObject jsonObject){
        editor.putString(LAST_WEATHER_DATA, jsonObject.toString());
        editor.commit();
    }

    public JSONObject getLastWeatherData(){
        try {
            String data = sharedPrefs.getString(LAST_WEATHER_DATA, null);
            if(null != data)
                return new JSONObject(data);
            else
                return null;
        } catch (JSONException e) {
            return null;
        }
    }

    public void setLocationPermanentlyDenied(Boolean isPermanentlyDenied){
        editor.putBoolean(IS_LOCATION_PERMANENTLY_DENIED, isPermanentlyDenied);
        editor.commit();
    }

    public Boolean isLocationPermanentlyDenied(){
        return sharedPrefs.getBoolean(IS_LOCATION_PERMANENTLY_DENIED, false);
    }

    public void setLocationPermissionSkipped(Boolean isPermissionSkipped){
        editor.putBoolean(IS_LOCATION_PERMISSION_SKIPPED, isPermissionSkipped);
        editor.commit();
    }

    public Boolean isLocationPermissionSkipped(){
        return sharedPrefs.getBoolean(IS_LOCATION_PERMISSION_SKIPPED, false);
    }

    public void setStoragePermanentlyDenied(Boolean isPermanentlyDenied){
        editor.putBoolean(IS_STORAGE_PERMANENTLY_DENIED, isPermanentlyDenied);
        editor.commit();
    }

    public Boolean isStoragePermanentlyDenied(){
        return sharedPrefs.getBoolean(IS_STORAGE_PERMANENTLY_DENIED, false);
    }

    public void setStoragePermissionSkipped(Boolean isPermissionSkipped){
        editor.putBoolean(IS_STORAGE_PERMISSION_SKIPPED, isPermissionSkipped);
        editor.commit();
    }

    public Boolean isStoragePermissionSkipped(){
        return sharedPrefs.getBoolean(IS_STORAGE_PERMISSION_SKIPPED, false);
    }

}
