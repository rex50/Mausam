package com.rex50.mausam.views.bottomsheets

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.enums.DownloadQuality
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.custom_text_views.RegularTextView
import kotlinx.android.synthetic.main.bs_download_quality.*

class BSDownloadQuality : MaterialBottomSheet(), View.OnClickListener{

    private val sharedPrefs: MausamSharedPrefs? by lazy {
        MausamApplication.getInstance()?.getSharedPrefs()
    }

    private val activeColorState: ColorStateList by lazy {
        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorAccent))
    }

    private val inActiveColorState: ColorStateList by lazy {
        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_to_white))
    }

    private var selectedView: Button? = null

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
        btnSmall?.setOnClickListener(this)
        btnRegular?.setOnClickListener (this)
        btnFull?.setOnClickListener(this)
        btnRaw?.setOnClickListener(this)

        selectedView = when(sharedPrefs?.photoDownloadQuality ?: DownloadQuality.FULL) {
            DownloadQuality.SMALL -> btnSmall
            DownloadQuality.REGULAR -> btnRegular
            DownloadQuality.FULL -> btnFull
            DownloadQuality.RAW -> btnRaw
        }

        setActive(selectedView)
    }

    private fun initDefaultCheckBox() {
        cbDefault?.typeface = Typeface.createFromAsset(requireContext().assets, RegularTextView.getFontPath())
        cbDefault?.isChecked = sharedPrefs?.showDownloadQuality == false
        cbDefault?.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs?.showDownloadQuality = !isChecked
        }
    }

    override fun onClick(v: View?) {
        /*setInActive(selectedView)
        selectedView = v as Button?
        setActive(selectedView)*/
        sharedPrefs?.photoDownloadQuality = when(v) {
            btnSmall -> {
                DownloadQuality.SMALL
            }

            btnRegular -> {
                DownloadQuality.REGULAR
            }

            btnFull -> {
                DownloadQuality.FULL
            }

            btnRaw -> {
                DownloadQuality.RAW
            }

            else -> DownloadQuality.FULL
        }
        dismiss()
        onSetSuccess?.invoke()
    }

    private fun setActive(button: Button?) {
        button?.apply {
            backgroundTintList = activeColorState
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun setInActive(button: Button?) {
        button?.apply {
            backgroundTintList = inActiveColorState
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white_to_black))
        }
    }

    companion object {

        const val TAG = "BSDownloadQuality"

        fun showQualitySheet(childFragmentManager: FragmentManager, onSet: () -> Unit) {
            BSDownloadQuality().let { quality ->
                quality.onSetSuccess = {
                    onSet()
                }
                quality.fromSettings = false
                quality.show(childFragmentManager)
            }
        }
    }

}