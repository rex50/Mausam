package com.rex50.mausam.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.rex50.mausam.enums.AutoWallpaperInterval;
import com.rex50.mausam.enums.DownloadQuality;

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
    private static final String DARK_MODE_ENABLED = "darkModeEnabled";
    private static final String FOLLOWING_SYSTEM_THEME = "followingSystemTheme";
    private static final String DATA_SAVER_MODE = "dataSaverMode";
    private static final String PHOTO_DOWNLOAD_QUALITY = "photoDownloadQuality";
    private static final String SHOW_DOWNLOAD_QUALITY = "showDownloadQuality";
    private static final String ENABLED_AUTO_WALLPAPER = "enabledAutoWallpaper";
    private static final String AUTO_WALLPAPER_INTERVAL = "autoWallpaperInterval";
    private static final String AUTO_WALLPAPER_BLUR_INTENSITY = "autoWallpaperBlurIntensity";

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

    public boolean isDarkModeEnabled() {
        return sharedPrefs.getBoolean(DARK_MODE_ENABLED, false);
    }

    public void setDarkModeEnabled(boolean darkModeEnabled){
        editor.putBoolean(DARK_MODE_ENABLED, darkModeEnabled).commit();
    }

    public boolean isFollowingSystemTheme() {
        return sharedPrefs.getBoolean(FOLLOWING_SYSTEM_THEME, false);
    }

    public void setFollowingSystemTheme(boolean isFollowing){
        editor.putBoolean(FOLLOWING_SYSTEM_THEME, isFollowing).commit();
    }

    public boolean isDataSaverMode() {
        return sharedPrefs.getBoolean(DATA_SAVER_MODE, false);
    }

    public void setDataSaverMode(boolean isFollowing){
        editor.putBoolean(DATA_SAVER_MODE, isFollowing).commit();
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

    public void setPhotoDownloadQuality(DownloadQuality quality) {
        editor.putString(PHOTO_DOWNLOAD_QUALITY, quality.getText());
        editor.commit();
    }

    public DownloadQuality getPhotoDownloadQuality() {
        return DownloadQuality.getQuality(sharedPrefs.getString(PHOTO_DOWNLOAD_QUALITY, "Full"));
    }

    public void setShowDownloadQuality(boolean show) {
        editor.putBoolean(SHOW_DOWNLOAD_QUALITY, show);
        editor.commit();
    }

    public boolean getShowDownloadQuality() {
        return sharedPrefs.getBoolean(SHOW_DOWNLOAD_QUALITY, true);
    }

    public void setEnabledAutoWallpaper(boolean show) {
        editor.putBoolean(ENABLED_AUTO_WALLPAPER, show);
        editor.commit();
    }

    public boolean isEnabledAutoWallpaper() {
        return sharedPrefs.getBoolean(ENABLED_AUTO_WALLPAPER, false);
    }


    public void setAutoWallpaperInterval(AutoWallpaperInterval interval) {
        editor.putString(AUTO_WALLPAPER_INTERVAL, interval.name());
        editor.commit();
    }

    public AutoWallpaperInterval getAutoWallpaperInterval() {
        return AutoWallpaperInterval.valueOf(sharedPrefs.getString(AUTO_WALLPAPER_INTERVAL, "TWENTY_FOUR_HOURS"));
    }

    public void setAutoWallpaperBlurIntensity(float intensity) {
        editor.putFloat(AUTO_WALLPAPER_BLUR_INTENSITY, intensity);
        editor.commit();
    }

    public float getAutoWallpaperBlurIntensity() {
        return sharedPrefs.getFloat(AUTO_WALLPAPER_BLUR_INTENSITY, 0f);
    }
}
