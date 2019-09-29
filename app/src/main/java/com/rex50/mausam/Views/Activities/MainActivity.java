package com.rex50.mausam.Views.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Network.APIManager;
import com.rex50.mausam.Network.LocationDataManager;
import com.rex50.mausam.R;
import com.rex50.mausam.Utils.GPSRequestHelper;
import com.rex50.mausam.Utils.MaterialSnackBar;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MainActivity extends BaseActivity{

    TextView initialText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(!sharedPrefs.getIsPermanentlyDenied()){
                startActivity(new Intent(this, PermissionActivity.class));
                finish();
            }
            else {
                //check for co-ordinates in sharedPrefs | initial current location | last searched location
            }
        }else {
            if(gpsRequestHelper.isGPSOn()){
                requestLocationAndWeather();
            }else {
                gpsRequestHelper.requestGPS(new GPSRequestHelper.GPSListener() {
                    @Override
                    public void enabled() {
                        requestLocationAndWeather();
                    }

                    @Override
                    public void disabled() {
                        materialSnackBar.show("GPS disabled", MaterialSnackBar.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void init() {
        initialText = findViewById(R.id.initialText);
        materialSnackBar.show("Getting location...", MaterialSnackBar.LENGTH_INDEFINITE);
    }

    private void requestLocationAndWeather(){
        LocationDataManager locationDataManager = LocationDataManager.getInstance(this);
        locationDataManager.getLocation(new LocationDataManager.LocationResultCallback() {
            @Override
            public void onSuccess(Location location) {
                materialSnackBar.show("Location Updated", MaterialSnackBar.LENGTH_SHORT);
                sharedPrefs.setLatitude(location.getLatitude());
                sharedPrefs.setLongitude(location.getLongitude());
                requestWeather(location);
            }

            @Override
            public void onFailure() {
                materialSnackBar.show("Error getting your location", MaterialSnackBar.LENGTH_SHORT);
            }
        });
    }

    private void requestWeather(Location location){
        APIManager apiManager = APIManager.getInstance(this);
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("lat", String.valueOf(location.getLatitude()));
        urlExtras.put("lon", String.valueOf(location.getLongitude()));
        apiManager.getCurrentWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.CallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(WeatherModelClass weatherDetails) {
                initialText.append(new DecimalFormat("##.##").format(weatherDetails.getMain().getTemp() - 273.15)+" C\n"+ weatherDetails.getName() +
                        "\n" + sharedPrefs.getLatitude()+ "," + sharedPrefs.getLongitude());

                materialSnackBar.showActionSnackBar("Current Temp in kelvin : " + weatherDetails.getMain().getTemp().toString(), "Ok",
                        MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
            }

            @Override
            public void onWeatherResponseFailure(String msg) {
                initialText.append("Something went wrong");
            }
        });
    }

}
