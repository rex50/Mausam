package com.rex50.mausam.network;

import android.content.Context;
import android.location.Location;

import com.rex50.mausam.interfaces.LocationResultListener;
import com.rex50.mausam.interfaces.WeatherResultListener;
import com.rex50.mausam.utils.DataParser;
import com.rex50.mausam.utils.MausamSharedPrefs;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.HashMap;

public class WeatherHelper {

    private Context context;
    private MausamSharedPrefs sharedPrefs;

    private WeatherHelper(){}

    WeatherHelper(Context context){
        this.context = context;
        sharedPrefs = new MausamSharedPrefs(context);
    }
    /**
     *
     * @param listener needed to get the location response
     */
    public void requestLocation(LocationResultListener listener){
        requestLocation(listener, null);
    }

    /**
     * This function can be used to get location (location data will be saved to shared prefs) and current weather
     * @param listener is needed to get weather details
     */
    public void requestLocationAndWeather(WeatherResultListener listener){
        requestLocation( null, listener);
    }

    /**
     *
     * @param locationListener needed to get response when location data is found. pass {null} if only weather info is needed.
     * @param weatherListener needed to get response of weather with location data. pass {null} if only location info is needed.
     */
    public void requestLocation(LocationResultListener locationListener, WeatherResultListener weatherListener){
//        materialSnackBar.show("Getting location...", MaterialSnackBar.LENGTH_INDEFINITE);
        MausamLocationManager locationDataManager = new MausamLocationManager();
        locationDataManager.getLocation(context, new MausamLocationManager.LocationResultCallback() {
            @Override
            public void onSuccess(Location location) {
//                materialSnackBar.dismiss();
                if(weatherListener != null){
                    //Get weather data only after 15 minutes as servers are updated at the interval of 10 minutes
                    if(sharedPrefs.getLastWeatherUpdated() != null){
                        DateTime currentTime = DateTime.now();
                        if(!sharedPrefs.getLastWeatherUpdated().isAfter(currentTime.minusMinutes(15))){
                            requestWeather(location.getLatitude(), location.getLongitude(), weatherListener);
                        }else {
                            DataParser parser = new DataParser();
                            weatherListener.onSuccess(parser.parseWeatherData(sharedPrefs.getLastWeatherData()));
                        }
                    }else {
                        requestWeather(location.getLatitude(), location.getLongitude(), weatherListener);
                    }
                }else {
                    if(locationListener != null)
                        locationListener.onSuccess(location);
                }
            }

            @Override
            public void onFailed(int errorCode) {
//                materialSnackBar.dismiss();
                if(weatherListener != null)
                    weatherListener.onFailed(errorCode);
                else if(locationListener != null)
                    locationListener.onFailed(errorCode);
                /*else
                    materialSnackBar.show("Error getting your location", MaterialSnackBar.LENGTH_LONG);*/
            }
        });
    }

    public void requestWeather(Double latitude, Double longitude, WeatherResultListener weatherListener){
        APIManager apiManager = APIManager.getInstance(context);
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("lat", String.valueOf(latitude));
        urlExtras.put("lon", String.valueOf(longitude));
        apiManager.getWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.WeatherAPICallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(JSONObject response) {
//                materialSnackBar.dismiss();
                sharedPrefs.setLastWeatherData(response);
                sharedPrefs.setLastWeatherUpdated(DateTime.now());
                DataParser parser = new DataParser();
                if(weatherListener != null)
                    weatherListener.onSuccess(parser.parseWeatherData(response));
            }

            @Override
            public void onWeatherResponseFailure(int errorCode, String msg) {
                /*if(sharedPrefs.getLatitude() != 0 && sharedPrefs.getLatitude() != 0 && weatherListener != null){
                    materialSnackBar.showActionSnackBar(msg,"RETRY", MaterialSnackBar.LENGTH_INDEFINITE, () -> {
                        requestWeather(sharedPrefs.getLatitude(), sharedPrefs.getLongitude(), weatherListener);
                        materialSnackBar.dismiss();
                    });
                }else*/
//                materialSnackBar.dismiss();
                if(weatherListener != null) {
                    weatherListener.onFailed(errorCode);
                }/*else {
                    materialSnackBar.showActionSnackBar(msg, "DISMISS", MaterialSnackBar.LENGTH_LONG, () -> materialSnackBar.dismiss());
                }*/

            }
        });
    }
}
