package com.rex50.mausam.Network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationDataManager {

    private static final long LOCATION_REFRESH_TIME = 10000;
    private static final float LOCATION_REFRESH_DISTANCE = 5000;
    public static String LAST_UPDATED_TIME;

    private static LocationDataManager instance;
    private Context ctx;


    private LocationDataManager(Context context) {
        this.ctx = context;
    }

    public static LocationDataManager getInstance(Context ctx){
        if (instance == null){
            instance = new LocationDataManager(ctx);
        }
        return instance;
    }

    /*private void getLocationDetails(Context context, LocationResultCallback listener) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(CURRENT_LOCATION.getLatitude(), CURRENT_LOCATION.getLongitude(), 1);
        CURRENT_CITY = addresses.get(0).getAddressLine(0);
        CURRENT_CITY = addresses.get(0).getLocality();
        CURRENT_STATE = addresses.get(0).getAddressLine(1);
        CURRENT_COUNTRY = addresses.get(0).getAddressLine(2);
        listener.onSuccess();
    }

    public boolean isGPSon() {
        try {
            return (Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.LOCATION_MODE) != 0);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }*/

    @SuppressLint("MissingPermission")
    public void getLocation(LocationResultCallback listener) {
//        FusedLocationProviderClient locClient = LocationServices.getFusedLocationProviderClient(context);
//        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
//        LocationCallback locationCallback = new LocationCallback(){
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                CURRENT_LOCATION = locationResult.getLastLocation();
//                LAST_UPDATED_TIME = DateFormat.getDateTimeInstance().format(new Date());
//            }
//        };
//
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(locationRequest);
//        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        LocationManager locMgr = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LAST_UPDATED_TIME = DateFormat.getDateTimeInstance().format(new Date());
                listener.onSuccess(location);
//                try {
//                    getLocationDetails(ctx,listener);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (locMgr != null) {
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
        }else {
            listener.onFailure();
        }
    }

    public interface LocationResultCallback {
        void onSuccess(Location location);
        void onFailure();
    }

}
