package com.rex50.mausam.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class LastLocationProvider {

    private MausamSharedPrefs sharedPrefs;

    public LastLocationProvider(Context context){
        sharedPrefs = new MausamSharedPrefs(context);
    }

    public Location getLastLocation(){
        if(sharedPrefs.getLongitude() != 0 && sharedPrefs.getLatitude() != 0){
            Location location = new Location("LAST_LOCATION");
            location.setLatitude(sharedPrefs.getLatitude());
            location.setLongitude(sharedPrefs.getLongitude());
            location.setExtras(jsonToBundle(sharedPrefs.getLastLocationDetails()));
            return location;
        }else
            return null;
    }

    public void updateLastLocation(Location location){
        sharedPrefs.setLatitude(location.getLatitude());
        sharedPrefs.setLongitude(location.getLongitude());
        if(null != location.getExtras()){
            Bundle locationExtras = location.getExtras();
            sharedPrefs.setLastLocationDetails(convertBundleToJSON(locationExtras));
        }
    }

    private JSONObject convertBundleToJSON(Bundle bundle){
        JSONObject object = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                object.put(key, JSONObject.wrap(bundle.get(key)));
            } catch(JSONException e) {
                try {
                    object.put(key, "");
                } catch (JSONException ignored) {
                }
            }
        }
        return object;
    }

    private static Bundle jsonToBundle(JSONObject jsonObject){
        Bundle bundle = new Bundle();
        if(null !=jsonObject){
            Iterator iter = jsonObject.keys();
            while(iter.hasNext()){
                String key = (String)iter.next();
                String value = null;
                try {
                    value = jsonObject.getString(key);
                } catch (JSONException e) {
                    value = "";
                }
                bundle.putString(key,value);
            }
        }
        return bundle;
    }
}