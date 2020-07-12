package com.rex50.mausam.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.utils.isStoragePermissionGranted
import com.rex50.mausam.views.MausamApplication

class ActSplash : BaseActivity() {

    companion object{
        const val SPLASH_TIME_OUT = 300
    }

    override val layoutResource: Int
        get() = R.layout.act_splash


    override fun loadAct(savedInstanceState: Bundle?) {
        //TODO: check for location permission is allowed or not, if not then open location activity else home
        MausamApplication.getInstance()?.setAppContext(applicationContext)
        Handler().postDelayed({
            GradientHelper.init(this)
            val intent: Intent = if (!isStoragePermissionGranted() || mausamSharedPrefs?.isFirstTime == true) {
                Intent(this@ActSplash, ActOnBoard::class.java)
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