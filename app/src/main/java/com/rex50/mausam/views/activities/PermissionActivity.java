package com.rex50.mausam.views.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rex50.mausam.R;
import com.rex50.mausam.utils.MaterialSnackBar;
import com.rex50.mausam.baseClasses.BaseActivity;

public class PermissionActivity extends BaseActivity implements View.OnClickListener {

    private int GPS_REQUEST = 99;
    private int LOCATION_REQUEST_CODE = 101;
    private int STORAGE_REQUEST_CODE = 102;
    private Switch locationSwitch, storageSwitch;
    private Button skipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        locationSwitch = findViewById(R.id.btn_ask_location_permission);
        storageSwitch = findViewById(R.id.btn_ask_storage_permission);
        skipBtn = findViewById(R.id.btn_skip_permission);

        if(isLocationPermissionGranted())
            locationSwitch.setChecked(true);

        if(isStoragePermissionGranted())
            storageSwitch.setChecked(true);

        if(isBothPermissionGranted()){
            startActivity(new Intent(this, MainActivity.class));
        }

        locationSwitch.setOnClickListener(this);
        storageSwitch.setOnClickListener(this);
        skipBtn.setOnClickListener(this);
    }

    private void requestLocationPermission() {
        // request the location permission
        if(!sharedPrefs.isLocationPermanentlyDenied()){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }else {
            materialSnackBar.showActionSnackBar("You have denied this permission. You can allow this permission from settings", "OK", MaterialSnackBar.LENGTH_INDEFINITE,
                    () -> materialSnackBar.dismiss());
        }

    }

    private void requestStoragePermission() {
        if(!sharedPrefs.isStoragePermanentlyDenied()){
            // request the storage permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_REQUEST_CODE);
            }
        }else {
            materialSnackBar.showActionSnackBar("You have denied this permission. You can allow this permission from settings", "OK", MaterialSnackBar.LENGTH_INDEFINITE,
                    () -> materialSnackBar.dismiss());
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_permission;
    }

    @Override
    protected void internetStatus(int internetType) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationSwitch.setChecked(true);
                sharedPrefs.setLocationPermissionSkipped(false);
                if(isBothPermissionGranted() || sharedPrefs.isStoragePermanentlyDenied()){
                    skipBtn.setVisibility(View.GONE);
                    materialSnackBar.show("Awesome! all permission are granted.", MaterialSnackBar.LENGTH_SHORT);
                    startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                    finish();
                } else {
                    materialSnackBar.show("Good, one more permission to go", MaterialSnackBar.LENGTH_SHORT);
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    materialSnackBar.showActionSnackBar("Location can't be detected automatically without this permission. So please allow Mausam this permission.", "OK", MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
                    locationSwitch.setChecked(false);
                }else {
                    locationSwitch.setChecked(false);
                    locationSwitch.setEnabled(false);
                    sharedPrefs.setLocationPermanentlyDenied(true);
                    materialSnackBar.showActionSnackBar("You have denied this permission earlier but can allow it from settings", "OK", MaterialSnackBar.LENGTH_INDEFINITE,
                            () -> {
                                if(sharedPrefs.isStoragePermanentlyDenied()){
                                    //TODO : start SearchActivity
                                }
                                materialSnackBar.dismiss();
                            });
                }
            }
        }else if(requestCode == STORAGE_REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                storageSwitch.setChecked(true);
                sharedPrefs.setStoragePermissionSkipped(false);
                if(isBothPermissionGranted()){
                    skipBtn.setVisibility(View.GONE);
                    materialSnackBar.show("Awesome! all permission are granted.", MaterialSnackBar.LENGTH_SHORT);
                    startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                    finish();
                }else if(sharedPrefs.isLocationPermanentlyDenied()){
                    //TODO : start search activity
                } else {
                    materialSnackBar.show("Good, one more permission to go", MaterialSnackBar.LENGTH_SHORT);
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    materialSnackBar.showActionSnackBar("Wallpaper download feature will not work without this permission. So please allow Mausam this permission.", "OK", MaterialSnackBar.LENGTH_INDEFINITE, () -> materialSnackBar.dismiss());
                    storageSwitch.setChecked(false);
                }else {
                    storageSwitch.setEnabled(false);
                    storageSwitch.setChecked(false);
                    sharedPrefs.setStoragePermanentlyDenied(true);
                    materialSnackBar.showActionSnackBar("You have denied this permission earlier but can allow it from settings", "OK", MaterialSnackBar.LENGTH_INDEFINITE,
                            () ->{
                                materialSnackBar.dismiss();
                                if(sharedPrefs.isLocationPermanentlyDenied()){
                                    //TODO : start SearchActivity
                                }
                            });
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isBothPermissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationPermissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isStoragePermissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ask_location_permission:
                requestLocationPermission();
                break;

            case R.id.btn_ask_storage_permission:
                requestStoragePermission();

            case R.id.btn_skip_permission: //start search activity
                if(!isStoragePermissionGranted() && !sharedPrefs.isStoragePermanentlyDenied())
                    sharedPrefs.setStoragePermissionSkipped(true);

                if(isLocationPermissionGranted()) {
                    //TODO: Show dialog before start MainActivity
                }else {
                    if(!sharedPrefs.isLocationPermanentlyDenied())
                        sharedPrefs.setLocationPermissionSkipped(true);
                    //TODO: Show dialog before start search screen
                }
                break;
        }
    }
}
