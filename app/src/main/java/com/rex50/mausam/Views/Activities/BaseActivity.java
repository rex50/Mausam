package com.rex50.mausam.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rex50.mausam.Interfaces.LocationResultListener;
import com.rex50.mausam.Interfaces.WeatherResultListener;
import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Network.APIManager;
import com.rex50.mausam.Network.MausamLocationManager;
import com.rex50.mausam.Recievers.NetworkChangeReceiver;
import com.rex50.mausam.Utils.DataParser;
import com.rex50.mausam.Utils.GPSRequestHelper;
import com.rex50.mausam.Utils.MaterialSnackBar;
import com.rex50.mausam.Utils.MausamSharedPrefs;
import com.rex50.mausam.Utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected MaterialSnackBar materialSnackBar;
    protected GPSRequestHelper gpsRequestHelper;
    protected MausamSharedPrefs sharedPrefs;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        materialSnackBar = new MaterialSnackBar(this, findViewById(android.R.id.content));
        gpsRequestHelper = new GPSRequestHelper(this);
        sharedPrefs = new MausamSharedPrefs(this);
        setContentView(getLayoutResource());
        networkChangeReceiver = new NetworkChangeReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int networkStatus = Utils.getConnectivityStatus(BaseActivity.this);
                if(networkStatus == Utils.TYPE_MOBILE){
                    internetStatus(Utils.TYPE_MOBILE);
                } else if(networkStatus == Utils.TYPE_WIFI){
                    internetStatus(Utils.TYPE_WIFI);
                } else if(networkStatus == Utils.TYPE_NOT_CONNECTED) {
                    internetStatus(Utils.TYPE_NOT_CONNECTED);
                }
            }
        };

    }

    protected abstract int getLayoutResource();

    protected abstract void internetStatus(int internetType);

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkChangeReceiver);
        super.onPause();
    }

    /**
     *
     * @param listener needed to get the location response
     */
    private void requestLocation(LocationResultListener listener){
        requestLocation(listener, null);
    }

    /**
     * This function can be used to get location (location data will be saved to shared prefs) and current weather
     * @param listener is needed to get weather details
     */
    protected void requestLocationAndWeather(WeatherResultListener listener){
        requestLocation( null, listener);
    }

    /**
     *
     * @param locationListener needed to get response when location data is found. pass {null} if only weather info is needed.
     * @param weatherListener needed to get response of weather with location data. pass {null} if only location info is needed.
     */
    private void requestLocation(LocationResultListener locationListener, WeatherResultListener weatherListener){
//        materialSnackBar.show("Getting location...", MaterialSnackBar.LENGTH_INDEFINITE);
        MausamLocationManager locationDataManager = new MausamLocationManager();
        locationDataManager.getLocation(this, new MausamLocationManager.LocationResultCallback() {
            @Override
            public void onSuccess(Location location) {
                materialSnackBar.dismiss();
                sharedPrefs.setLatitude(location.getLatitude());
                sharedPrefs.setLongitude(location.getLongitude());
                if(weatherListener != null)
                    requestWeather(location.getLatitude(), location.getLongitude(), weatherListener);
                else if(locationListener != null)
                    locationListener.onSuccess(location);
            }

            @Override
            public void onFailed(int errorCode) {
                materialSnackBar.dismiss();
                if(weatherListener != null)
                    weatherListener.onFailed(errorCode);
                else if(locationListener != null)
                    locationListener.onFailed(errorCode);
                else
                    materialSnackBar.show("Error getting your location", MaterialSnackBar.LENGTH_LONG);
            }
        });
    }

    protected void requestWeather(Double latitude, Double longitude, WeatherResultListener weatherListener){
        APIManager apiManager = APIManager.getInstance(this);
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("lat", String.valueOf(latitude));
        urlExtras.put("lon", String.valueOf(longitude));
        apiManager.getCurrentWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.CallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(JSONObject response) {
                materialSnackBar.dismiss();
                sharedPrefs.setLastWeatherData(response);
                DataParser parser = new DataParser();
                if(weatherListener != null)
                    weatherListener.onSuccess(parser.parseWeatherData(response));
                /*else {
                    toggleLocationLoader(false);
                    Fragment fragment = HomeFragment.newInstance(weatherDetails);
                    loadFragment(fragment);
                }*/
            }

            @Override
            public void onWeatherResponseFailure(int errorCode, String msg) {
                /*if(sharedPrefs.getLatitude() != 0 && sharedPrefs.getLatitude() != 0 && weatherListener != null){
                    materialSnackBar.showActionSnackBar(msg,"RETRY", MaterialSnackBar.LENGTH_INDEFINITE, () -> {
                        requestWeather(sharedPrefs.getLatitude(), sharedPrefs.getLongitude(), weatherListener);
                        materialSnackBar.dismiss();
                    });
                }else*/
                materialSnackBar.dismiss();
                if(weatherListener != null) {
                    weatherListener.onFailed(errorCode);
                }else {
                    materialSnackBar.showActionSnackBar(msg, "DISMISS", MaterialSnackBar.LENGTH_LONG, () -> materialSnackBar.dismiss());
                }

            }
        });
    }

    /*public boolean isInternetAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        } catch (Exception ignored) {
        }
        return false;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPSRequestHelper.GPS_REQUEST_CODE) {
            gpsRequestHelper.setGPSrequestResponse();
        }
    }
}
