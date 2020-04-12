package com.rex50.mausam.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;
import com.rex50.mausam.utils.MaterialSnackBar;
import com.rex50.mausam.utils.MausamSharedPrefs;
import com.rex50.mausam.baseClasses.BaseActivity;
import com.rex50.mausam.views.fragments.SearchFragment;

public class SearchCityActivity extends BaseActivity implements SearchFragment.OnFragmentInteractionListener{

    private FragmentManager fragmentManager;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        searchFragment = SearchFragment.newInstance(true);
        loadFragment(searchFragment);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search_city;
    }

    @Override
    protected void internetStatus(int internetType) {
        //TODO : handle according to internet Status
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.search_city_fragment_placeholder, fragment, fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onWeatherSearchSuccess(WeatherModelClass weatherDetails) {

    }

    @Override
    public void nextBtnClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public MausamSharedPrefs getSharedPrefs() {
        return sharedPrefs;
    }

    @Override
    public MaterialSnackBar getSnackBar() {
        return materialSnackBar;
    }

}
