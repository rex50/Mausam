package com.rex50.mausam.Interfaces;

import com.rex50.mausam.ModelClasses.WeatherModelClass;

public interface WeatherResultListener {
    void onSuccess(WeatherModelClass weatherDetails);
    void onFailed(int errorCode);
}
