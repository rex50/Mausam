package com.rex50.mausam.views.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.transition.Explode
import android.util.Log
import android.view.Window
import androidx.core.content.contentValuesOf
import com.bumptech.glide.util.Util
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.MausamApplication
import com.rex50.mausam.views.activities.ActSettings.CalculateCacheSizeTask.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.act_settings.*
import kotlinx.android.synthetic.main.header_custom_general.*

class ActSettings : BaseActivity() {

    override val layoutResource: Int
        get() = R.layout.act_settings

    override fun loadAct(savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {

        tvPageTitle?.text = getString(R.string.settings)

        gradientLine?.background = GradientHelper.getInstance(this)?.getRandomLeftRightGradient()

        val isDarkMode = mausamSharedPrefs?.isDarkModeEnabled ?: false

        sDarkMode.setChecked(isDarkMode)

        tvDarkModeDesc?.text = if (isDarkMode) getString(R.string.dark_mode_on_desc) else getString(R.string.dark_mode_off_desc)

        sDataSaver?.setChecked(mausamSharedPrefs?.isDataSaverMode ?: false)

        //tvCacheDesc?.text = getString(R.string.cache_desc, getString(R.string.calculating))

        lnlCurrLoc?.hideView()

        calcAppCache()

        initClicks()
    }

    private fun initClicks() {

        sDarkMode?.setOnCheckedChangeListener {
            mausamSharedPrefs?.isDarkModeEnabled = it
            Handler().postDelayed({
                MausamApplication.getInstance()?.followSystemThemeIfRequired()
            }, 600)
        }

        sDataSaver?.setOnCheckedChangeListener {
            mausamSharedPrefs?.isDataSaverMode = it
        }

        btnClearCache?.setOnClickListener{
            showToast("Clearing cache...")
            clearAppCache()
        }

        btnCurrentLoc?.setOnClickListener{
            showToast("Work in progress")
        }

        PushDownAnim
                .setPushDownAnimTo(lnlBigDonation, lnlSmallDonation, lnlMediumDonation, btnShareApp, btnGotoPlayStore, btnReportBugs)
                .setOnClickListener {
                    when(it){
                        lnlSmallDonation -> {
                            showToast("Small Donation")
                        }

                        lnlMediumDonation -> {
                            showToast("Medium Donation")
                        }

                        lnlBigDonation -> {
                            showToast("Big Donation")
                        }

                        btnShareApp -> {
                            showToast("Share App")
                        }

                        btnGotoPlayStore -> {
                            showToast("Rate App")
                        }

                        btnReportBugs -> {
                            showToast("Report bugs")
                        }
                    }
                }
    }

    private fun clearAppCache(){
        ClearCacheTask(this, object: ClearCacheTask.CacheClearCallback{
            override fun onCleared() {
                calcAppCache()
            }
        }).execute()
    }

    private fun calcAppCache(){
        CalculateCacheSizeTask(this, object : CacheSizeCallback{
            override fun onCalculated(size: Long?) {
                Log.e("calcAppCache", "onCalculated: " + size)
                var mb = 0
                size?.apply {
                    mb = (size/(1000*1000)).toInt()
                }
                showSize(mb)
            }
        }).execute()
    }

    class ClearCacheTask(val context: Context?,private val callback: CacheClearCallback?) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean? {
            return Utils.deleteCache(context)
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            context?.apply {
                //if(result == true)
                    //showToast("Cleared cache")
                /*else
                    showToast("Error while clearing cache")*/
                callback?.onCleared()
            }
        }

        interface CacheClearCallback{
            fun onCleared()
        }

    }

    class CalculateCacheSizeTask(val context: Context?, private val callback: CacheSizeCallback?): AsyncTask<Void, Void, Long>(){

        override fun doInBackground(vararg params: Void?): Long {
            return Utils.folderSize(Utils.getAppCacheDir(context))
        }

        override fun onPostExecute(result: Long?) {
            super.onPostExecute(result)
            callback?.onCalculated(result)
        }

        interface CacheSizeCallback{
            fun onCalculated(size: Long?)
        }

    }

    private fun showSize(result: Int) {
        tvCacheDesc?.text = getString(R.string.using_cache, result.toString())
    }



    override fun internetStatus(internetType: Int) {
        //TODO
    }
}