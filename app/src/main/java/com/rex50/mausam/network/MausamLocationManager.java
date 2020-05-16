package com.rex50.mausam.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.rex50.mausam.utils.LastLocationProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.rex50.mausam.utils.Utils.*;

public class MausamLocationManager {
    /*

        //Done : Try this https://stackoverflow.com/questions/29657948/get-the-current-location-fast-and-once-in-android for location.

        private static MausamLocationManager instance;
        private Context context;


        private MausamLocationManager(Context context) {
            this.context = context;
        }

        public static MausamLocationManager getInstance(Context ctx){
            if (instance == null){
                instance = new MausamLocationManager(ctx);
            }
            return instance;
        }

        */
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
    }*//*


    @SuppressLint("MissingPermission")
    public void getLocation(LocationResultCallback listener) {

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = false;
        if (locationManager != null) {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
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
            */
/*boolean isNetworkEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {*//*

            //Done(not possible) : search for getting location through internet
//                Criteria criteria = new Criteria();
//                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                locationManager.requestSingleUpdate(criteria, new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        listener.onSuccess(location);
//                    }
//                    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
//                    @Override public void onProviderEnabled(String provider) { }
//                    @Override public void onProviderDisabled(String provider) { }
//                }, null);
//            }
        }
    }

    public interface LocationResultCallback {
        void onSuccess(Location location);
        void onFailure();
    }
*/
    private LocationManager locationManager;
    private LocationResultCallback locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private String TAG = "MausamLocationManager";
    private LastLocationProvider lastLocationProvider;
    private Context context;

    public void getLocation(Context context, LocationResultCallback result) {
        //Check for permissions first
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            result.onFailed(NO_PERMISSION);
            return;
        }
        this.context = context;
        lastLocationProvider = new LastLocationProvider(context);
        locationResult = result;
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            if (lastLocationProvider.getLastLocation() != null) {
                result.onSuccess(lastLocationProvider.getLastLocation());
            }
            else
                result.onFailed(GPS_NOT_ENABLED);
        }else {
            Criteria criteria = new Criteria();
            if (gps_enabled) {
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
//                locationManager.requestSingleUpdate(criteria, locationListenerGps, null);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 100, locationListenerGps, null);
            }
            if (network_enabled) {
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                locationManager.requestSingleUpdate(criteria, locationListenerNetwork, null);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 100, locationListenerGps, null);
            }
        }
    }

    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(locationResult != null){
                Log.d(TAG, "onLocationChanged: locationListenerGps");
                locationManager.removeUpdates(locationListenerNetwork);
                locationManager.removeUpdates(this);
                lastLocationProvider.updateLastLocation(getLocationDetails(context, location));
                locationResult.onSuccess(location);
            }
            locationResult = null;
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(locationResult != null){
                Log.d(TAG, "onLocationChanged: locationListenerNetwork");
                locationManager.removeUpdates(locationListenerGps);
                locationManager.removeUpdates(this);
                lastLocationProvider.updateLastLocation(getLocationDetails(context, location));
                locationResult.onSuccess(location);
            }
            locationResult = null;
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private Location getLocationDetails(Context context, Location location){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            return location;
        }

        String currentCity, currentState, countryName, featureName;

        currentCity = optString(addresses.get(0).getLocality());
        currentState = optString(addresses.get(0).getAdminArea());
        countryName = optString(addresses.get(0).getCountryName());
        featureName = optString(addresses.get(0).getFeatureName());

        Bundle bundle = new Bundle();
        bundle.putString("city", currentCity);
        bundle.putString("state", currentState);
        bundle.putString("country", countryName);
        bundle.putString("feature", featureName);

        location.setExtras(bundle);
//        listener.onSuccess();
        return location;
    }

    private String optString(String value){
        if(null != value)
            return value;
        else
            return "null";
    }

    public interface LocationResultCallback {
        void onSuccess(Location location);
        void onFailed(int errorCode);
    }

}
