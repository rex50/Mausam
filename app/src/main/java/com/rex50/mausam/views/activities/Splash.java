package com.rex50.mausam.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import com.rex50.mausam.R;

public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: check for location permission is allowed or not, if not then open location activity else home
        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(() -> {
            Intent intent;
            if(ContextCompat.checkSelfPermission(Splash.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && !sharedPrefs.getIsPermanentlyDenied()){
                intent = new Intent(Splash.this, PermissionActivity.class);
            }else {
                intent = new Intent(Splash.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void internetStatus(int internetType) {

    }
}
