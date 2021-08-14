package com.rex50.mausam.views.activities

import android.content.Intent
import android.os.Bundle
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.utils.isStoragePermissionGranted
import com.rex50.mausam.MausamApplication
import kotlinx.coroutines.*

class ActSplash : BaseActivity() {

    companion object{
        const val SPLASH_TIME_OUT = 200
    }

    override val layoutResource: Int
        get() = R.layout.act_splash


    override fun loadAct(savedInstanceState: Bundle?) {
        CoroutineScope(Dispatchers.Main).launch {
            val newIntent = withContext(Dispatchers.IO) {
                MausamApplication.getInstance()?.setAppContext(applicationContext)
                GradientHelper.init(applicationContext)
                if (!isStoragePermissionGranted() || mausamSharedPrefs?.isFirstTime == true) {
                    Intent(this@ActSplash, ActOnBoard::class.java)
                } else {
                    Intent(this@ActSplash, ActMain::class.java)
                }
            }
            delay(SPLASH_TIME_OUT.toLong())
            startActivity(newIntent)
            finish()
        }
    }

    override fun internetStatus(internetType: Int) {}
}