package com.rex50.mausam.Network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.rex50.mausam.Views.MainActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationDataManager {

    private static final long LOCATION_REFRESH_TIME = 10000;
    private static final float LOCATION_REFRESH_DISTANCE = 5000;
    public static String CURRENT_CITY;
    public static Location CURRENT_LOCATION;
    public static String LAST_UPDATED_TIME;
    public static String CURRENT_STATE;
    public static String CURRENT_COUNTRY;
//    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
//    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private LocationResultResponse resultResponse;
    private static LocationDataManager instance;
    private Context ctx;



//    TODO: create this singleton class and store all current location when instantiated
    private LocationDataManager(Context context) {
        resultResponse = (MainActivity) context;
        this.ctx = context;
    }

    public static LocationDataManager getInstance(Context ctx){
        if (instance == null){
            instance = new LocationDataManager(ctx);
        }
        return instance;
    }

    private void getLocationDetails(Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(CURRENT_LOCATION.getLatitude(), CURRENT_LOCATION.getLongitude(), 1);
        CURRENT_CITY = addresses.get(0).getAddressLine(0);
        CURRENT_STATE = addresses.get(0).getAddressLine(1);
        CURRENT_COUNTRY = addresses.get(0).getAddressLine(2);
        if(resultResponse != null){
            resultResponse.LocationUpdated();
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
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
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                CURRENT_LOCATION = location;
                LAST_UPDATED_TIME = DateFormat.getDateTimeInstance().format(new Date());
                try {
                    getLocationDetails(ctx);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, listener);
        }else {
            Toast.makeText(ctx, "Error getting Location, Try again later", Toast.LENGTH_SHORT).show();
        }
    }

    public interface LocationResultResponse{
        void LocationUpdated();
    }

}
