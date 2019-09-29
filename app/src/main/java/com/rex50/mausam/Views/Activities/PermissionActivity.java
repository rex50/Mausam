package com.rex50.mausam.Views.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.L;
import com.rex50.mausam.R;
import com.rex50.mausam.Utils.MaterialSnackBar;

public class PermissionActivity extends BaseActivity implements View.OnClickListener {

    private int GPS_REQUEST = 99;
    private int LOCATION_REQUEST_CODE = 101;
    private TextView btn_ask_permission, btn_skip_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        btn_ask_permission = findViewById(R.id.btn_ask_location_permission);
        btn_skip_permission = findViewById(R.id.btn_skip_permission);

        btn_skip_permission.setOnClickListener(this);
        btn_ask_permission.setOnClickListener(this);
    }

    private void requestPermission() {
            // No explanation needed; request the permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_permission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                materialSnackBar.show("Granted !!!", MaterialSnackBar.LENGTH_SHORT);
                startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                finish();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    materialSnackBar.show("Denied", MaterialSnackBar.LENGTH_SHORT);
                }else {
                    sharedPrefs.setIsPermanentlyDenied(true);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ask_location_permission:
                requestPermission();
                break;
            case R.id.btn_skip_permission: //start search activity
                break;
        }
    }
}
