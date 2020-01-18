package com.rex50.mausam.interfaces;

import com.rex50.mausam.model_classes.weather.WeatherModelClass;

public interface WeatherResultListener {
    void onSuccess(WeatherModelClass weatherDetails);
    void onFailed(int errorCode);
}
