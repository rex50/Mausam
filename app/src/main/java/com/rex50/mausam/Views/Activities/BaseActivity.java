package com.rex50.mausam.Views.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rex50.mausam.Utils.GPSRequestHelper;
import com.rex50.mausam.Utils.MaterialSnackBar;
import com.rex50.mausam.Utils.MausamSharedPrefs;

public abstract class BaseActivity extends AppCompatActivity {

    protected MaterialSnackBar materialSnackBar;
    protected GPSRequestHelper gpsRequestHelper;
    protected MausamSharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        materialSnackBar = new MaterialSnackBar(this, findViewById(android.R.id.content));
        gpsRequestHelper = new GPSRequestHelper(this);
        sharedPrefs = new MausamSharedPrefs(this);
        setContentView(getLayoutResource());

    }

    protected abstract int getLayoutResource();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GPSRequestHelper.GPS_REQUEST_CODE) {
            gpsRequestHelper.setGPSrequestResponse();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
