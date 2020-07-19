package com.rex50.mausam.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.StringDef;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.rex50.mausam.R;
import com.rex50.mausam.base_classes.BaseActivity;
import com.rex50.mausam.interfaces.LocationResultListener;
import com.rex50.mausam.interfaces.WeatherResultListener;
import com.rex50.mausam.model_classes.utils.MoreListData;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;
import com.rex50.mausam.network.APIManager;
import com.rex50.mausam.network.MausamLocationManager;
import com.rex50.mausam.storage.MausamSharedPrefs;
import com.rex50.mausam.utils.Constants;
import com.rex50.mausam.utils.CustomViewPager;
import com.rex50.mausam.utils.DataParser;
import com.rex50.mausam.utils.FlashyTabBar;
import com.rex50.mausam.utils.LastLocationProvider;
import com.rex50.mausam.utils.MaterialSnackBar;
import com.rex50.mausam.utils.custom_text_views.SemiBoldTextView;
import com.rex50.mausam.views.fragments.FragFavourites;
import com.rex50.mausam.views.fragments.FragHome;
import com.rex50.mausam.views.fragments.FragSearch;
import com.rex50.mausam.views.fragments.FragSearchResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.rex50.mausam.utils.Utils.TYPE_NOT_CONNECTED;
import static com.rex50.mausam.utils.Utils.getConnectivityStatus;

//import com.google.firebase.analytics.FirebaseAnalytics;

//import com.cuberto.flashytabbarandroid.TabFlashyAnimator;

public class ActMain extends BaseActivity implements
        FragSearch.OnFragmentInteractionListener,
        FragFavourites.OnFragmentInteractionListener,
        FragHome.OnFragmentInteractionListener,
        FragSearchResult.OnFragmentInteractionListener {

    private String TAG = "ActMain";
    private FragmentManager fragmentManager;
    private LinearLayout locationLoader;
    private CustomErrorPage noInternetErrorPage, noGpsErrorPage, noPermissionErrorPage;
    private FragHome fragHome;
    private FragSearch fragSearch;
    private FragFavourites fragFav;
    private Boolean isGettingWeather = false, isFirstWeatherFetched = false;
    private LastLocationProvider lastLocationProvider;
    private FlashyTabBar tabFlashyAnimator;
    private FragmentStatePagerAdapter fragmentAdapter;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
            Constants.ListModes.LIST_MODE_POPULAR_PHOTOS,
            Constants.ListModes.LIST_MODE_USER_PHOTOS,
            Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS,
            Constants.ListModes.LIST_MODE_COLLECTIONS,
            Constants.ListModes.LIST_MODE_USER_COLLECTIONS})
    public @interface photosListMode {
    }

    @Override
    protected void loadAct(@Nullable Bundle savedInstanceState) {
        //Firebase init
        /*FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        instanceId.getInstanceId();*/
        /*FirebaseAnalytics analytis = FirebaseAnalytics.getInstance(this);
        analytis.logEvent("on_main_activity", new Bundle());*/

        init();
        /*if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(sharedPrefs.isLocationPermissionSkipped()){
                if(sharedPrefs.getLongitude() != 0 && sharedPrefs.getLatitude() != 0){
                    requestWeather(sharedPrefs.getLatitude(),sharedPrefs.getLongitude());
                    materialSnackBar.show("Showing last known location weather", MaterialSnackBar.LENGTH_SHORT);
                }else
                    loadFragment(new SearchFragment());
            }else if(!sharedPrefs.isLocationPermanentlyDenied()){
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
        JSONObject weatherJSON = mausamSharedPrefs.getLastWeatherData();
        if(weatherJSON != null){
            /*if(!isPermissionEnabled())
                materialSnackBar.showActionSnackBar(getString(R.string.no_permission_error_msg), "OK", MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
            else if(Utils.getConnectivityStatus(this) == TYPE_NOT_CONNECTED)
                materialSnackBar.showActionSnackBar(getString(R.string.no_internet_error_msg), "OK", MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
            DataParser parser = new DataParser();
            WeatherModelClass weatherDetails = parser.parseWeatherData(weatherJSON);
            HomeFragment homeFragment = HomeFragment.newInstance(weatherDetails);
            loadFragment(homeFragment);*/
        }else {
            noInternetErrorPage.show();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_main;
    }

    private void init() {
        lastLocationProvider = new LastLocationProvider(this);
        prepareFragments();
//        tabFlashyAnimator.setBadge(3,2);
//        tabFlashyAnimator.setBadge(1, 2);
        locationLoader = findViewById(R.id.location_loader);
        noGpsErrorPage = new CustomErrorPage(R.drawable.location_permission, getString(R.string.no_gps_error_msg));
        noInternetErrorPage = new CustomErrorPage(R.drawable.no_internet, getString(R.string.no_internet_error_msg));
        noPermissionErrorPage = new CustomErrorPage(R.drawable.location_permission, getString(R.string.no_permission_error_msg));
        fragmentManager = getSupportFragmentManager();
        toggleLocationLoader(false);
    }

    private void prepareFragments() {
        CustomViewPager viewPager = findViewById(R.id.homeViewPager);
        viewPager.setPagingEnabled(false);
        List<Fragment> mFragmentList = new ArrayList<>();
        fragHome = new FragHome();
        fragSearch = new FragSearch();
        fragFav = new FragFavourites();

        mFragmentList.add(fragHome);
        mFragmentList.add(fragSearch);
        mFragmentList.add(fragFav);
        viewPager.setAdapter(getFragmentAdapter(mFragmentList));
        viewPager.setOffscreenPageLimit(mFragmentList.size());

        setupTabLayout(viewPager);
    }

    private FragmentStatePagerAdapter getFragmentAdapter(List<Fragment> mFragmentList) {
         return new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
    }

    private void setupTabLayout(CustomViewPager viewPager) {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabFlashyAnimator = new FlashyTabBar(tabLayout);
        tabFlashyAnimator.addTabItem("Home", R.drawable.ic_logo);
        tabFlashyAnimator.addTabItem("Search", R.drawable.ic_search);
        tabFlashyAnimator.addTabItem("Favourites", R.drawable.ic_heart);
        tabFlashyAnimator.highLightTab(0);
        viewPager.addOnPageChangeListener(tabFlashyAnimator);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*if(tab.getPosition() == 0){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }*/
                if(tab.getPosition() == 1 && fragSearch != null){
                    fragSearch.setFocusToSearchBox(false);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(fragSearch != null)
                    fragSearch.setFocusToSearchBox(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(tabFlashyAnimator != null)
            tabFlashyAnimator.onStart(findViewById(R.id.tabLayout));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(tabFlashyAnimator != null)
            tabFlashyAnimator.onStop();
    }

    private void getWeatherDetails(){
        isGettingWeather = true;

        /*requestLocation(new LocationResultListener() {
            @Override
            public void onSuccess(Location location) {

            }

            @Override
            public void onFailed(int errorCode) {

            }
        });*/

        /*requestLocationAndWeather(new WeatherResultListener() {
            @Override
            public void onSuccess(WeatherModelClass weatherDetails) {
                isGettingWeather = false;
                isFirstWeatherFetched = true;
                toggleLocationLoader(false);
//                toggleLocationPermissionError(false);
                noPermissionErrorPage.hide();
                homeFragment = HomeFragment.newInstance(weatherDetails);
//                loadFragment(homeFragment);
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
                        if(getConnectivityStatus(ActMain.this) != TYPE_NOT_CONNECTED){
                            materialSnackBar.show("No internet connection", MaterialSnackBar.LENGTH_INDEFINITE);
                        }
                        break;
                }
            }
        });*/
    }

    @Override
    protected void internetStatus(int internetType) {
        if(internetType == TYPE_NOT_CONNECTED) {
            JSONObject weatherJSON = mausamSharedPrefs.getLastWeatherData();
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
        if(fragment1 != null && fragment1.isAdded() || fragment instanceof FragHome){
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
    public void onWeatherSearchSuccess(WeatherModelClass weatherDetails) {
        fragmentManager.popBackStack();
        toggleLocationLoader(false);
        FragSearchResult fragment = new FragSearchResult();
        fragment.setWeatherDetails(weatherDetails);
        loadFragment(fragment, true);
    }

    @Override
    public void nextBtnClicked() {

    }

    @Override
    public MausamSharedPrefs getSharedPrefs() {
        return mausamSharedPrefs;
    }

    @Override
    public MaterialSnackBar getSnackBar() {
        return null;
    }

    @Override
    public void startSearchScreen() {
        loadFragment(new FragSearch(), true);
    }

    @Override
    public Bundle getLastLocationDetails() {
        return lastLocationProvider.getLastLocation().getExtras();
    }

    @Override
    public void requestWeather(WeatherResultListener listener) {
        /*requestLocationAndWeather(new WeatherResultListener() {
            @Override
            public void onSuccess(WeatherModelClass weatherDetails) {
                isGettingWeather = false;
                isFirstWeatherFetched = true;
                toggleLocationLoader(false);
//                toggleLocationPermissionError(false);
                noPermissionErrorPage.hide();
                listener.onSuccess(weatherDetails);
                *//*homeFragment = HomeFragment.newInstance(weatherDetails);
                loadFragment(homeFragment);*//*
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
                        if(getConnectivityStatus(ActMain.this) != TYPE_NOT_CONNECTED){
                            materialSnackBar.show("No internet connection", MaterialSnackBar.LENGTH_INDEFINITE);
                        }
                        break;
                }
            }
        });*/
        if(mausamSharedPrefs.getLastWeatherData() != null){
            WeatherModelClass weatherModelClass = DataParser.parseWeatherData(mausamSharedPrefs.getLastWeatherData());
            requestWeather(weatherModelClass.getCoord().getLat(), weatherModelClass.getCoord().getLon(), listener);
        }else {
            //TODO : listener.onFailed();
        }
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

    @Override
    public void startMorePhotosActivity(@NotNull MoreListData data) {
        startActivity(new Intent(this, ActPhotosList.class)
                .putExtra(Constants.IntentConstants.LIST_DATA, data)
        );
    }

    @Override
    public void startMoreFeaturedCollections(@NotNull MoreListData data) {
        startActivity(new Intent(this, ActCollectionsList.class)
                .putExtra(Constants.IntentConstants.SEARCH_FEAT_COLLECTION, true)
                .putExtra(Constants.IntentConstants.LIST_DATA, data)
        );
    }

    @Nullable
    @Override
    public MaterialSnackBar getMaterialSnackBar() {
        return materialSnackBar;
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
                Glide.with(ActMain.this).load(errorImgId).into(locationErrorImg);
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

    @Override
    public void startSettings() {
        startActivity(new Intent(this, ActSettings.class));
    }

    private void toggleSearchLoader(boolean state){
        if(state){
            //show loader
        }
        else{
            //hide loader
        }
    }

    /**
     *
     * @param listener needed to get the location response
     */
    protected void requestLocation(LocationResultListener listener){
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
                if(weatherListener != null){
                    //Get weather data only after 15 minutes as servers are updated at the interval of 10 minutes
                    if(mausamSharedPrefs.getLastWeatherUpdated() != null){
                        DateTime currentTime = DateTime.now();
                        if(!mausamSharedPrefs.getLastWeatherUpdated().isAfter(currentTime.minusMinutes(15))){
                            requestWeather(location.getLatitude(), location.getLongitude(), weatherListener);
                        }else {
                            weatherListener.onSuccess(DataParser.parseWeatherData(mausamSharedPrefs.getLastWeatherData()));
                        }
                    }else {
                        requestWeather(location.getLatitude(), location.getLongitude(), weatherListener);
                    }
                }else {
                    if(locationListener != null)
                        locationListener.onSuccess(location);
                }
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
        apiManager.getWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.WeatherAPICallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(JSONObject response) {
                materialSnackBar.dismiss();
                mausamSharedPrefs.setLastWeatherData(response);
                mausamSharedPrefs.setLastWeatherUpdated(DateTime.now());
                if(weatherListener != null)
                    weatherListener.onSuccess(DataParser.parseWeatherData(response));
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

}
