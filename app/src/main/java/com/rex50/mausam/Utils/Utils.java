package com.rex50.mausam.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //For location and weather
    public static final int GPS_NOT_ENABLED = 5;
    public static final int NO_PERMISSION = 6;
    public static final int LAST_LOCATION_NOT_FOUND = 7;
    public static final int WEATHER_NOT_FOUND = 8;
    public static final int CITY_NOT_FOUND = 9;
    public static final int PAGE_NOT_FOUND = 404;

    //For Internet connection
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;

    private static boolean containsNumericValues(String text){
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    private static boolean containsSpecialChars(String text){
        Pattern p = Pattern.compile("[^A-Za-z0-9 ]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static void validateText(String text,TextValidationInterface listner){
        if(text.trim().isEmpty()) {
            listner.empty();
        } else if(containsNumericValues(text)){
            listner.containNumber();
        } else if(containsSpecialChars(text)){
            listner.containSpecialChars();
        } else {
            listner.correct();
        }
    }

    public interface TextValidationInterface{
        void correct();
        void containNumber();
        void containSpecialChars();
        void empty();
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

}
