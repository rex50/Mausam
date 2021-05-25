package com.rex50.mausam.base_classes

import android.R
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.transition.Explode
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.rex50.mausam.receivers.NetworkChangeReceiver
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.GPSRequestHelper
import com.rex50.mausam.utils.MaterialSnackBar
import com.rex50.mausam.utils.Utils

abstract class BaseActivity : AppCompatActivity() {
    @JvmField
    protected var materialSnackBar: MaterialSnackBar? = null
    protected var gpsRequestHelper: GPSRequestHelper? = null
    @JvmField
    protected var mausamSharedPrefs: MausamSharedPrefs? = null
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mausamSharedPrefs = MausamSharedPrefs(this)
        materialSnackBar = MaterialSnackBar(this, findViewById(R.id.content))
        gpsRequestHelper = GPSRequestHelper(this)
        setContentView(layoutResource)
        loadAct(savedInstanceState)
        networkChangeReceiver = object : NetworkChangeReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val networkStatus = Utils.getConnectivityStatus(this@BaseActivity)
                if (networkStatus == Utils.TYPE_MOBILE) {
                    internetStatus(Utils.TYPE_MOBILE)
                } else if (networkStatus == Utils.TYPE_WIFI) {
                    internetStatus(Utils.TYPE_WIFI)
                } else if (networkStatus == Utils.TYPE_NOT_CONNECTED) {
                    internetStatus(Utils.TYPE_NOT_CONNECTED)
                }
                //internetStatus(networkStatus)
            }
        }

    }

    protected abstract fun loadAct(savedInstanceState: Bundle?)

    protected abstract val layoutResource: Int

    protected open fun internetStatus(internetType: Int) {}

    override fun onResume() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(networkChangeReceiver)
        super.onPause()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPSRequestHelper.GPS_REQUEST_CODE) {
            gpsRequestHelper!!.setGPSrequestResponse()
        }
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}