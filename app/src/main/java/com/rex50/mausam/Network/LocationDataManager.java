package com.rex50.mausam.Network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationDataManager {

    //TODO : Try this https://stackoverflow.com/questions/29657948/get-the-current-location-fast-and-once-in-android for location.

    private static LocationDataManager instance;
    private Context context;


    private LocationDataManager(Context context) {
        this.context = context;
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
            return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE) != 0);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }*/

    @SuppressLint("MissingPermission")
    public void getLocation(LocationResultCallback listener) {

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    listener.onSuccess(location);
                }
                @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                @Override public void onProviderEnabled(String provider) { }
                @Override public void onProviderDisabled(String provider) { }
            }, null);
        }else {
            boolean isNetworkEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        listener.onSuccess(location);
                    }
                    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                    @Override public void onProviderEnabled(String provider) { }
                    @Override public void onProviderDisabled(String provider) { }
                }, null);
            }
        }
    }

    public interface LocationResultCallback {
        void onSuccess(Location location);
        void onFailure();
    }

}
