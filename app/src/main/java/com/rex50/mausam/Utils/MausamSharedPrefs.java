package com.rex50.mausam.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.util.ConcurrentModificationException;

public class MausamSharedPrefs {

    private static final String SHARED_PREFS_NAME = "MausamSharedPrefs";
    private static final String IS_FIRST_TIME = "isFirstTime";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String IS_PERMANENTLY_DENIED = "isPermissionDeniedPermanently";
    private static final String IS_PERMISSION_SKIPPED = "isPermissionSkipped";

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

    public void setIsPermanentlyDenied(Boolean isPermanentlyDenied){
        editor.putBoolean(IS_PERMANENTLY_DENIED, isPermanentlyDenied);
        editor.commit();
    }

    public Boolean getIsPermanentlyDenied(){
        return sharedPrefs.getBoolean(IS_PERMANENTLY_DENIED, false);
    }

    public void setIsPermissionSkipped(Boolean isPermissionSkipped){
        editor.putBoolean(IS_PERMISSION_SKIPPED, isPermissionSkipped);
        editor.commit();
    }

    public Boolean getIsPermissionSkipped(){
        return sharedPrefs.getBoolean(IS_PERMISSION_SKIPPED, false);
    }
}
