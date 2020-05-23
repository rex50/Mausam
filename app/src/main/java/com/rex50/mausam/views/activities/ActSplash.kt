package com.rex50.mausam.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity

class ActSplash : BaseActivity() {

    companion object{
        const val SPLASH_TIME_OUT = 300
    }

    override fun getLayoutResource(): Int = R.layout.act_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: check for location permission is allowed or not, if not then open location activity else home
        Handler().postDelayed({
            val intent: Intent = if (ContextCompat.checkSelfPermission(this@ActSplash,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && !mausamSharedPrefs!!.isLocationPermanentlyDenied) {
                Intent(this@ActSplash, ActPermission::class.java)
            } else {
                Intent(this@ActSplash, ActMain::class.java)
            }
            //TODO: remove below line while releasing
//            intent = new Intent(Splash.this, SearchCityActivity.class);
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    override fun internetStatus(internetType: Int) {}
}