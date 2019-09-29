package com.rex50.mausam.Views.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.rex50.mausam.R;

public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: check for location permission is allowed or not, if not then open location activity else home
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(this, PermissionActivity.class));
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }
}
