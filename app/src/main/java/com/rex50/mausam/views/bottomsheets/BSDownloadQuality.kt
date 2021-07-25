package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.enums.DownloadQuality
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.custom_text_views.MediumTextView
import com.rex50.mausam.utils.showToast
import kotlinx.android.synthetic.main.bs_download_quality.*
import java.lang.RuntimeException

class BSDownloadQuality : MaterialBottomSheet(){

    private val sharedPrefs: MausamSharedPrefs? by lazy {
        MausamApplication.getInstance()?.getSharedPrefs()
    }

    var onSetSuccess: (() -> Unit)? = null

    private var fromSettings = true

    override fun layoutId(): Int = R.layout.bs_download_quality

    fun show(fragmentManager: FragmentManager): BSDownloadQuality {
        if(!fromSettings && sharedPrefs?.showDownloadQuality == false)
            onSetSuccess?.invoke()
        else
            showNow(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        initQualityButtons()

        initDefaultCheckBox()

    }

    private fun initQualityButtons() {

        val qualityMap = hashMapOf<DownloadQuality, Int>().apply {
            put(DownloadQuality.SMALL, R.id.rbSmall)
            put(DownloadQuality.REGULAR, R.id.rbRegular)
            put(DownloadQuality.FULL, R.id.rbFull)
            put(DownloadQuality.RAW, R.id.rbRaw)
        }

        //Show last selected quality
        val selectedId = qualityMap[sharedPrefs?.photoDownloadQuality ?: DownloadQuality.FULL]
        selectedId?.takeIf { it != -1 }?.let { id ->
            rgPhotoQuality?.check(id)
        } ?: FirebaseCrashlytics.getInstance().recordException(RuntimeException("Entry not found"))


        rgPhotoQuality?.setOnCheckedChangeListener { _, checkedId ->
            //Find selected quality and store in shared prefs
            val selected = qualityMap.entries.find { checkedId == it.value }
            selected?.let {
                sharedPrefs?.photoDownloadQuality = it.key
            } ?: let {
                val msg = "We are facing a problem while starting download"
                showToast(msg)
                FirebaseCrashlytics.getInstance().recordException(RuntimeException("$msg: Entry not found"))
            }
        }

        if(fromSettings)
            btnDownloadPhoto?.text = getString(R.string.apply_changes)

        //Handle click event for download button
        btnDownloadPhoto?.setOnClickListener { _ ->
            onSetSuccess?.invoke()
            dismiss()
        }

    }

    private fun initDefaultCheckBox() {
        cbDefault?.typeface = MediumTextView.getTypeFace(requireContext())
        cbDefault?.isChecked = sharedPrefs?.showDownloadQuality == true
        cbDefault?.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs?.showDownloadQuality = isChecked
        }
    }

    companion object {

        const val TAG = "BSDownloadQuality"

        fun showQualitySheet(childFragmentManager: FragmentManager, isDownloaded: Boolean = false, onSet: () -> Unit) {
            if(!isDownloaded) {
                BSDownloadQuality().let { quality ->
                    quality.onSetSuccess = {
                        onSet()
                    }
                    quality.fromSettings = false
                    quality.show(childFragmentManager)
                }
            } else
                onSet()
        }
    }

}