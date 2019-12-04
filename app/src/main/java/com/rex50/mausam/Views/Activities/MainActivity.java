package com.rex50.mausam.Views.Activities;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.rex50.mausam.Interfaces.WeatherResultListener;
import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.R;
import com.rex50.mausam.Utils.CustomTextViews.SemiBoldTextView;
import com.rex50.mausam.Utils.DataParser;
import com.rex50.mausam.Utils.GPSRequestHelper;
import com.rex50.mausam.Utils.MaterialSnackBar;
import com.rex50.mausam.Utils.Utils;
import com.rex50.mausam.Views.Fragments.HomeFragment;
import com.rex50.mausam.Views.Fragments.SearchFragment;
import com.rex50.mausam.Views.Fragments.SearchResultFragment;

import org.json.JSONObject;

import static com.rex50.mausam.Utils.Utils.*;

public class MainActivity extends BaseActivity implements
        SearchFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        SearchResultFragment.OnFragmentInteractionListener {

    private String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    private LinearLayout locationLoader;
    private CustomErrorPage noInternetErrorPage, noGpsErrorPage, noPermissionErrorPage;
    private Fragment homeFragment;
    private Boolean isGettingWeather = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        /*if(ContextCompat.checkSelfPermission(this,
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
                requestLocation(true);
            }else {
                //show dialog and ask for gps
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
                alertDialogBuilder.setMessage("Allow GPS to get current position location");
                alertDialogBuilder.setPositiveButton("OK",
                        (arg0, arg1) -> gpsRequestHelper.requestGPS(new GPSRequestHelper.GPSListener() {
                            @Override
                            public void enabled() {
                                requestLocation(true);
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
        }*/

        if(getConnectivityStatus(this) != TYPE_NOT_CONNECTED && isPermissionEnabled()) {
            toggleLocationLoader(true);
            getWeatherDetails();
        } else {
            loadOfflineWeatherData();
        }
    }

    private boolean isPermissionEnabled() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void loadOfflineWeatherData() {
        JSONObject weatherJSON = sharedPrefs.getLastWeatherData();
        if(weatherJSON != null){
            if(!isPermissionEnabled())
                materialSnackBar.showActionSnackBar(getString(R.string.no_permission_error_msg), "OK", MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
            else if(Utils.getConnectivityStatus(this) == TYPE_NOT_CONNECTED)
                materialSnackBar.showActionSnackBar(getString(R.string.no_internet_error_msg), "OK", MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
            DataParser parser = new DataParser();
            WeatherModelClass weatherDetails = parser.parseWeatherData(weatherJSON);
            HomeFragment homeFragment = HomeFragment.newInstance(weatherDetails);
            loadFragment(homeFragment);
        }else {
            noInternetErrorPage.show();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void init() {
        locationLoader = findViewById(R.id.location_loader);
        noGpsErrorPage = new CustomErrorPage(R.drawable.location_permission, getString(R.string.no_gps_error_msg));
        noInternetErrorPage = new CustomErrorPage(R.drawable.no_internet, getString(R.string.no_internet_error_msg));
        noPermissionErrorPage = new CustomErrorPage(R.drawable.location_permission, getString(R.string.no_permission_error_msg));
        fragmentManager = getSupportFragmentManager();
        toggleLocationLoader(false);
    }

    private void getWeatherDetails(){
        isGettingWeather = true;
        requestLocationAndWeather(new WeatherResultListener() {
            @Override
            public void onSuccess(WeatherModelClass weatherDetails) {
                isGettingWeather = false;
                toggleLocationLoader(false);
//                toggleLocationPermissionError(false);
                noPermissionErrorPage.hide();
                homeFragment = HomeFragment.newInstance(weatherDetails);
                loadFragment(homeFragment);
            }

            @Override
            public void onFailed(int errorCode) {
                isGettingWeather = false;
                toggleLocationLoader(false);
                materialSnackBar.dismiss();
                switch (errorCode){
                    case NO_PERMISSION :
//                        toggleLocationPermissionError(true);
                        noPermissionErrorPage.show();
                        break;

                    case GPS_NOT_ENABLED :
//                        materialSnackBar.show("GPS is turned off, trying to get last known location", MaterialSnackBar.LENGTH_INDEFINITE);
//                        toggleLocationGPSError(true);
                        noGpsErrorPage.show();
                        materialSnackBar.showActionSnackBar("GPS is off", "Enable", MaterialSnackBar.LENGTH_INDEFINITE,
                                () -> {
//                                    toggleLocationGPSError(false);
                                    noGpsErrorPage.hide();
                                    toggleLocationLoader(true);
                                    materialSnackBar.dismiss();

                                    //TODO : Prompt to start GPS and on success get weather details
                                    gpsRequestHelper.requestGPS(new GPSRequestHelper.GPSListener() {
                                        @Override
                                        public void enabled() {
                                            getWeatherDetails();
                                            materialSnackBar.dismiss();
                                        }

                                        @Override
                                        public void disabled() {
                                            //TODO : do something without location.
                                            materialSnackBar.dismiss();
                                        }
                                    });
                                });
                        break;

                    case CITY_NOT_FOUND : //TODO : city data is not available in database
                        break;

                    case PAGE_NOT_FOUND :
                        if(getConnectivityStatus(MainActivity.this) != TYPE_NOT_CONNECTED){
                            materialSnackBar.show("No internet connection", MaterialSnackBar.LENGTH_INDEFINITE);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void internetStatus(int internetType) {
        if(internetType == TYPE_NOT_CONNECTED) {
            JSONObject weatherJSON = sharedPrefs.getLastWeatherData();
            if(weatherJSON == null){
                noInternetErrorPage.show();
                removeAllFragments();
            }
        } else{
            if(!isGettingWeather && isPermissionEnabled()){
                getWeatherDetails();
            }
            if(noInternetErrorPage.isShowing)
                noInternetErrorPage.hide();
        }
    }

    private void loadFragment(Fragment fragment){
        loadFragment(fragment, false);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        Fragment fragment1 = fragmentManager.findFragmentByTag(fragment.getTag());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment1 != null && fragment1.isAdded() || fragment instanceof HomeFragment){
            fragmentTransaction.replace(R.id.home_content, fragment, fragment.getTag());
        }else{
            fragmentTransaction.add(R.id.home_content, fragment, fragment.getTag());
        }
        if(addToBackStack)
            fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void removeAllFragments(){
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
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

    private void toggleLocationLoader(boolean state){
        Animation locationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_scale_alpha);
        runOnUiThread(() -> {
            // Stuff that updates the UI
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
        });
    }

    /*private void toggleLocationGPSError(boolean state){
        runOnUiThread(() -> {
            Glide.with(this).load(R.drawable.location_permission).into(locationErrorImg);
            errorMsgTxt.setText(getString(R.string.no_gps_error_msg));
            locationError.setVisibility(state ? View.VISIBLE : View.GONE);
        });
    }*/

    /*private void toggleLocationPermissionError(boolean state){
        runOnUiThread(() -> {
            Glide.with(this).load(R.drawable.location_permission).into(locationErrorImg);
            errorMsgTxt.setText(getString(R.string.no_permission_error_msg));
            locationError.setVisibility(state ? View.VISIBLE : View.GONE);
        });
    }*/

    private class CustomErrorPage{

        private ImageView locationErrorImg;
        private SemiBoldTextView errorMsgTxt;
        private LinearLayout errorLayout;
        private int errorImgId;
        private String errorMsg;
        private boolean isShowing = false;

        private CustomErrorPage(int errorImgId, String errorMsg){
            errorLayout = findViewById(R.id.location_error);
            locationErrorImg = findViewById(R.id.location_error_img);
            errorMsgTxt = findViewById(R.id.error_msg_txt);
            this.errorImgId = errorImgId;
            this.errorMsg = errorMsg;
        }

        private void show(){
            runOnUiThread(() -> {
                Glide.with(MainActivity.this).load(errorImgId).into(locationErrorImg);
                errorMsgTxt.setText(errorMsg);
                errorLayout.setVisibility(View.VISIBLE);
                isShowing = true;
            });
        }

        private void hide(){
            runOnUiThread(() -> {
                errorLayout.setVisibility(View.GONE);
                isShowing = false;
            });
        }

        private boolean isShowing(){
            return isShowing;
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
