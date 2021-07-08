package com.rex50.mausam.views.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.rex50.mausam.BuildConfig
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.*
import com.rex50.mausam.MausamApplication
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

        tvPrivacyDesc?.text = getString(R.string.privacy_desc, BuildConfig.VERSION_NAME)

        tvPageDesc?.hideView()

        gradientLine?.background = GradientHelper.getInstance(this)?.getRandomLeftRightGradient()

        val isDarkMode = mausamSharedPrefs?.isDarkModeEnabled ?: false

        val isDataSaverMode = mausamSharedPrefs?.isDataSaverMode ?: false

        initDarkModeFields(isDarkMode)

        sDataSaver?.setChecked(isDataSaverMode)

        lnlCurrLoc?.hideView()

        calcAppCache()

        initClicks()
    }

    private fun initDarkModeFields(isDarkMode: Boolean){
        sDarkMode?.isEnabled = true
        sDarkMode.setChecked(isDarkMode)
        flHeaderBg?.takeIf { isDarkMode }?.showView() ?: flHeaderBg?.hideView()
        ivHeaderImg?.setImageResource(R.drawable.ic_darth_vader)
        tvDarkModeDesc?.text = if (isDarkMode) getString(R.string.dark_mode_on_desc) else getString(R.string.dark_mode_off_desc)
    }

    private fun initClicks() {

        sDarkMode?.setOnCheckedChangeListener {
            mausamSharedPrefs?.isDarkModeEnabled = it
            sDarkMode?.isEnabled = false
            Handler().postDelayed({
                MausamApplication.getInstance()?.followSystemThemeIfRequired()
                initDarkModeFields(it)
            }, 500)
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
                .setPushDownAnimTo(lnlBigDonation, lnlSmallDonation, lnlMediumDonation, btnShareApp, btnGotoPlayStore, btnReportBugs, btnResUsed)
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

                        btnResUsed -> {
                            val intent = Intent(this@ActSettings,ActUsedLibrary::class.java)
                            startActivity(intent)
                        }

                        else -> showToast("Something thing is wrong. Please try again.")
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