package com.rex50.mausam.utils

import android.content.Context
import android.net.*
import android.os.Build


class ConnectionChecker(val context: Context) {

    fun isNetworkConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network: Network?
            if (connectivityManager == null) {
                return false
            } else {
                network = connectivityManager.activeNetwork

                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network) ?: return false

                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                ) {
                    return true
                }
            }
        } else {
            if (connectivityManager == null) {
                return false
            }
            return if (connectivityManager.activeNetworkInfo == null) {
                false
            } else connectivityManager.activeNetworkInfo!!.isConnected
        }
        return false
    }

}