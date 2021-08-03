package com.rex50.mausam.views.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.enums.AutoWallpaperInterval
import com.rex50.mausam.enums.DownloadQuality
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.custom_text_views.MediumTextView
import com.rex50.mausam.utils.showToast
import kotlinx.android.synthetic.main.bs_auto_wallpaper_interval.*
import java.lang.RuntimeException

class BSAutoWallpaperInterval : MaterialBottomSheet(){

    private val sharedPrefs: MausamSharedPrefs? by lazy {
        MausamApplication.getInstance()?.getSharedPrefs()
    }

    /**
     * This is invoked when done button is clicked or when dismissed
     *
     * @return TRUE: if value is changed, FALSE: When no change has been made or selected same interval
     */
    var onSetSuccess: ((Boolean) -> Unit)? = null

    private var lastInterval: AutoWallpaperInterval? = null

    var selected: MutableMap.MutableEntry<AutoWallpaperInterval, Int>? = null
        private set

    private val qualityMap = hashMapOf<AutoWallpaperInterval, Int>().apply {
        put(AutoWallpaperInterval.ONE_HOUR, R.id.rb1Hour)
        put(AutoWallpaperInterval.THREE_HOURS, R.id.rb3Hours)
        put(AutoWallpaperInterval.TWELVE_HOURS, R.id.rb12Hours)
        put(AutoWallpaperInterval.TWENTY_FOUR_HOURS, R.id.rb24Hours)
    }

    override fun layoutId(): Int = R.layout.bs_auto_wallpaper_interval

    fun show(fragmentManager: FragmentManager): BSAutoWallpaperInterval {
        showNow(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        showSelectedInterval()

        initIntervalButtons()

        initDoneClick()

    }

    private fun initDoneClick() {
        btnDone?.setOnClickListener { _ ->
            dismiss()
        }
    }

    private fun showSelectedInterval() {

        lastInterval = sharedPrefs?.autoWallpaperInterval

        //Show last selected interval
        val selectedId = qualityMap[lastInterval ?: AutoWallpaperInterval.TWENTY_FOUR_HOURS]
        selectedId?.takeIf { it != -1 }?.let { id ->
            rgInterval?.check(id)
        } ?: FirebaseCrashlytics.getInstance().recordException(RuntimeException("Entry not found"))

    }

    private fun initIntervalButtons() {

        rgInterval?.setOnCheckedChangeListener { _, checkedId ->

            //Find selected interval and store in shared prefs
            selected = qualityMap.entries.find { checkedId == it.value }

            selected?.let { entry ->

                sharedPrefs?.autoWallpaperInterval = entry.key

            } ?: let {

                val msg = "We are facing a problem while changing interval"
                showToast(msg)
                FirebaseCrashlytics.getInstance().recordException(RuntimeException("$msg: Entry not found"))

            }
        }

    }

    companion object {
        const val TAG = "BSAutoWallpaperInterval"
    }

    override fun onDismiss(dialog: DialogInterface) {
        onSetSuccess?.invoke(lastInterval == selected?.key)
        super.onDismiss(dialog)
    }

}