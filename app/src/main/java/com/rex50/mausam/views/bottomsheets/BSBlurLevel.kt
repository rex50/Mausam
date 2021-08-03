package com.rex50.mausam.views.bottomsheets

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.rex50.imageblur.ImageBlur
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.storage.MausamSharedPrefs
import kotlinx.android.synthetic.main.bs_blur_level.*
import kotlin.math.roundToInt

class BSBlurLevel: MaterialBottomSheet() {

    private val sharedPrefs: MausamSharedPrefs? by lazy {
        MausamApplication.getInstance()?.getSharedPrefs()
    }

    var onDismissed: (() -> Unit)? = null

    val previewPattern: Bitmap by lazy {
        BitmapFactory.decodeResource(requireContext().resources, R.drawable.pattern)
    }

    override fun layoutId(): Int = R.layout.bs_blur_level

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        val selectedProgress = (sharedPrefs?.autoWallpaperBlurIntensity ?: 0f)

        initPreview(selectedProgress)

        initSeekBar((selectedProgress * 4).roundToInt())

        btnDone?.setOnClickListener {
            dismiss()
        }
    }

    private fun initPreview(selectedProgress: Float) {
        ImageBlur.with(requireContext())
            .load(previewPattern)
            .intensity(selectedProgress)
            .into(ivPreview)
    }

    private fun initSeekBar(selectedProgress: Int) {

        sBlur?.progress = selectedProgress
        sBlur?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //Divide to make it no more than 25
                (progress.toFloat() / 4).let {
                    sharedPrefs?.autoWallpaperBlurIntensity = if(it < 1) 0f else it
                    initPreview(it)
                }

                tvDownloadDesc?.text = getString(R.string.desc_bs_blur_level, "$progress%")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissed?.invoke()
        super.onDismiss(dialog)
    }

}