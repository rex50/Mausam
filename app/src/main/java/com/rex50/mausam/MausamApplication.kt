package com.rex50.mausam

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.ExistingPeriodicWorkPolicy
import com.rex50.mausam.di.UIDependencySetup
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.workers.ChangeWallpaperWorker
import java.io.File

class MausamApplication : Application(){

    companion object{
        private var context: Context? = null

        private var app: MausamApplication? = null

        private var mausamSharedPrefs: MausamSharedPrefs? = null

        @JvmStatic
        var appFolder: String = ""

        @JvmStatic
        fun getInstance() : MausamApplication? = let {
            if(app == null){
                app = MausamApplication()
            }
            app
        }

    }


    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()

        appFolder = applicationContext.getExternalFilesDir("Downloads")?.path + File.separator

        checkIfThemeChangeRequired()

        injectDependency()

        verifyAutoWallWorkerIsRunning()

    }

    private fun injectDependency() {
        UIDependencySetup.inject(applicationContext)
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
        appFolder = applicationContext.getExternalFilesDir("Downloads")?.path + File.separator
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    override fun onLowMemory() {
        super.onLowMemory()

    }

    fun setAppContext(app: Context){
        context = app
    }

    fun getAppContext() = context

    fun getSharedPrefs(): MausamSharedPrefs? {

        if(context == null){
            context = applicationContext
        }

        if(mausamSharedPrefs == null)
            mausamSharedPrefs = MausamSharedPrefs(context)

        return mausamSharedPrefs
    }

    private fun checkIfThemeChangeRequired() {
        //Check if user has enabled force dark mode
        //enableLightModeIfRequired()
        //enableDarkModeIfRequired()
        getSharedPrefs()
        followSystemThemeIfRequired()
    }

    fun followSystemThemeIfRequired(){
        if(mausamSharedPrefs?.isDarkModeEnabled != true && mausamSharedPrefs?.isFollowingSystemTheme != true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            mausamSharedPrefs?.apply {
                isDarkModeEnabled = false
                isFollowingSystemTheme = true
            }
        }else{
            enableDarkModeIfRequired()
        }
    }

    fun enableLightModeIfRequired(){
        //val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(mausamSharedPrefs?.isDarkModeEnabled != true /*&& isNightTheme == Configuration.UI_MODE_NIGHT_YES*/) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            mausamSharedPrefs?.apply {
                isDarkModeEnabled = false
                isFollowingSystemTheme = false
            }
        }
    }

    fun enableDarkModeIfRequired(){
        //val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(mausamSharedPrefs?.isDarkModeEnabled != false /*&& isNightTheme == Configuration.UI_MODE_NIGHT_NO*/){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            mausamSharedPrefs?.apply {
                isDarkModeEnabled = true
                isFollowingSystemTheme = false
            }
        }
    }


    //Verify if auto wallpaper enabled or not.
    // If enabled then keep the existing work in the queue.
    // If not then add the work to the queue
    private fun verifyAutoWallWorkerIsRunning() {
        ChangeWallpaperWorker.scheduleAutoWallpaper(applicationContext, ExistingPeriodicWorkPolicy.KEEP)
    }

}