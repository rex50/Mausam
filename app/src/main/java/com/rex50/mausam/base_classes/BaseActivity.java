package com.rex50.mausam.base_classes;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rex50.mausam.recievers.NetworkChangeReceiver;
import com.rex50.mausam.utils.GPSRequestHelper;
import com.rex50.mausam.utils.MaterialSnackBar;
import com.rex50.mausam.storage.MausamSharedPrefs;
import com.rex50.mausam.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected MaterialSnackBar materialSnackBar;
    protected GPSRequestHelper gpsRequestHelper;
    protected MausamSharedPrefs sharedPrefs;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        materialSnackBar = new MaterialSnackBar(this, findViewById(android.R.id.content));
        gpsRequestHelper = new GPSRequestHelper(this);
        sharedPrefs = new MausamSharedPrefs(this);
        setContentView(getLayoutResource());
        networkChangeReceiver = new NetworkChangeReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int networkStatus = Utils.getConnectivityStatus(BaseActivity.this);
                if(networkStatus == Utils.TYPE_MOBILE){
                    internetStatus(Utils.TYPE_MOBILE);
                } else if(networkStatus == Utils.TYPE_WIFI){
                    internetStatus(Utils.TYPE_WIFI);
                } else if(networkStatus == Utils.TYPE_NOT_CONNECTED) {
                    internetStatus(Utils.TYPE_NOT_CONNECTED);
                }
            }
        };

    }

    protected abstract int getLayoutResource();

    protected abstract void internetStatus(int internetType);

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkChangeReceiver);
        super.onPause();
    }

    /*public boolean isInternetAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        } catch (Exception ignored) {
        }
        return false;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPSRequestHelper.GPS_REQUEST_CODE) {
            gpsRequestHelper.setGPSrequestResponse();
        }
    }
}
