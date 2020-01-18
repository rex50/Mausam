package com.rex50.mausam.interfaces;

import android.location.Location;

public interface LocationResultListener {
    void onSuccess(Location location);
    void onFailed(int errorCode);
}
