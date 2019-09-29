package com.rex50.mausam.Utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

public class GPSRequestHelper {

    public static final int GPS_REQUEST_CODE = 99;
    private Activity activity;
    private GPSListener listener;

    public GPSRequestHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean isGPSOn() {
        try {
            return (Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE) != 0);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void requestGPS(GPSListener listener) {
        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
        this.listener = listener;
    }

    public void setGPSrequestResponse() {
        try {
            if ((Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE) == 0)) {
                listener.disabled();
            } else {
                listener.enabled();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface GPSListener {
        void enabled();

        void disabled();
    }


}
