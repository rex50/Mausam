package com.rex50.mausam.Views.Activities;


import android.Manifest;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Network.APIManager;
import com.rex50.mausam.Network.LocationDataManager;
import com.rex50.mausam.R;
import com.rex50.mausam.Utils.GPSRequestHelper;
import com.rex50.mausam.Utils.MaterialSnackBar;
import com.rex50.mausam.Views.Fragments.HomeFragment;
import com.rex50.mausam.Views.Fragments.SearchFragment;
import com.rex50.mausam.Views.Fragments.SearchResultFragment;

import java.util.HashMap;

public class MainActivity extends BaseActivity implements
        SearchFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        SearchResultFragment.OnFragmentInteractionListener {

    private String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    private LinearLayout locationLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(sharedPrefs.getIsPermissionSkipped()){
                if(sharedPrefs.getLongitude() != 0 && sharedPrefs.getLatitude() != 0){
                    requestWeather(sharedPrefs.getLatitude(),sharedPrefs.getLongitude());
                    materialSnackBar.show("Showing last known location weather", MaterialSnackBar.LENGTH_SHORT);
                }else
                    loadFragment(new SearchFragment());
            }else if(!sharedPrefs.getIsPermanentlyDenied()){
                startActivity(new Intent(this, PermissionActivity.class));
                finish();
            }
            else {
                //check for co-ordinates in sharedPrefs | initial current location | last searched location
                if(sharedPrefs.getLongitude() != 0 && sharedPrefs.getLatitude() != 0){
                    requestWeather(sharedPrefs.getLatitude(),sharedPrefs.getLongitude());
                    materialSnackBar.show("Showing last known location weather", MaterialSnackBar.LENGTH_SHORT);
                }else
                    loadFragment(new SearchFragment());
            }
        }else {
            if(gpsRequestHelper.isGPSOn()){
                requestLocationAndWeather();
            }else {
                //show dialog and ask for gps
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                alertDialogBuilder.setMessage("Allow GPS to get current position location");
                alertDialogBuilder.setPositiveButton("OK",
                        (arg0, arg1) -> gpsRequestHelper.requestGPS(new GPSRequestHelper.GPSListener() {
                            @Override
                            public void enabled() {
                                requestLocationAndWeather();
                            }

                            @Override
                            public void disabled() {
                                if(sharedPrefs.getLongitude() != 0 && sharedPrefs.getLatitude() != 0){
                                    requestWeather(sharedPrefs.getLatitude(),sharedPrefs.getLongitude());
                                    materialSnackBar.show("GPS is off. Showing last known location weather", MaterialSnackBar.LENGTH_SHORT);
                                } else{
                                    loadFragment(new SearchFragment());
                                    materialSnackBar.show("GPS is off", MaterialSnackBar.LENGTH_SHORT);
                                }
                            }
                        }));



                alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                    if(sharedPrefs.getLongitude() != 0 && sharedPrefs.getLatitude() != 0){
                        requestWeather(sharedPrefs.getLatitude(),sharedPrefs.getLongitude());
                        materialSnackBar.show("GPS is off. Showing known location weather", MaterialSnackBar.LENGTH_SHORT);
                    } else{
                        loadFragment(new SearchFragment());
                        materialSnackBar.show("GPS is off", MaterialSnackBar.LENGTH_SHORT);
                    }
                });

                alertDialogBuilder.setCancelable(false);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void init() {
        locationLoader = findViewById(R.id.location_loader);
        toggleLocationLoader(false);
    }

    private void loadFragment(Fragment fragment){
        loadFragment(fragment, false);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.home_content, fragment);
        if(addToBackStack)
            fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void requestLocationAndWeather(){
        toggleLocationLoader(true);
        materialSnackBar.show("Getting location...", MaterialSnackBar.LENGTH_INDEFINITE);
        LocationDataManager locationDataManager = LocationDataManager.getInstance(this);
        locationDataManager.getLocation(new LocationDataManager.LocationResultCallback() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: ");
                materialSnackBar.dismiss();
                sharedPrefs.setLatitude(location.getLatitude());
                sharedPrefs.setLongitude(location.getLongitude());
                requestWeather(location.getLatitude(),location.getLongitude());
            }

            @Override
            public void onFailure() {
                materialSnackBar.show("Error getting your location", MaterialSnackBar.LENGTH_SHORT);
            }
        });
    }

    private void requestWeather(Double latitude, Double longitude){
        APIManager apiManager = APIManager.getInstance(this);
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("lat", String.valueOf(latitude));
        urlExtras.put("lon", String.valueOf(longitude));
        apiManager.getCurrentWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.CallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(WeatherModelClass weatherDetails) {
                toggleLocationLoader(false);
                Fragment fragment = HomeFragment.newInstance(weatherDetails);
                loadFragment(fragment);
            }

            @Override
            public void onWeatherResponseFailure(String msg) {
                materialSnackBar.show(msg, MaterialSnackBar.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void goBack() {
//        fragmentManager.popBackStack();
        onBackPressed();
    }

    @Override
    public void onSearchSuccess(WeatherModelClass weatherDetails) {
        fragmentManager.popBackStack();
        toggleLocationLoader(false);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setWeatherDetails(weatherDetails);
        loadFragment(fragment, true);
    }

    @Override
    public void startSearchScreen() {
        loadFragment(new SearchFragment(), true);
    }

    public void toggleLocationLoader(boolean state){
        Animation locationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_scale_alpha);
        if(state){
            locationLoader.setVisibility(View.VISIBLE);
            locationLoader.startAnimation(locationAnimation);
        }
        else{
            locationLoader.clearAnimation();
            locationAnimation.cancel();
            locationAnimation.reset();
            locationLoader.setVisibility(View.GONE);
        }
    }

    private void toggleSearchLoader(boolean state){
        if(state){
            //show loader
        }
        else{
            //hide loader
        }
    }

}
