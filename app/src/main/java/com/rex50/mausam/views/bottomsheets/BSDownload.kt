package com.rex50.mausam.views.bottomsheets

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.enums.DownloadQuality
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.Constants.Network.NO_INTERNET
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.showToast
import com.rex50.mausam.utils.showView
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.bs_download.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BSDownload(private var fragManager: FragmentManager) : MaterialBottomSheet() {

    var isDownloading = false

    var onCancel: (() -> Unit)? = null

    override fun layoutId(): Int = R.layout.bs_download

    override fun setupBehaviour(bottomSheetBehavior: BottomSheetBehavior<*>?) {
        bottomSheetBehavior?.apply {
            isHideable = false
            isDraggable = false
        }
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        isDownloading = true
        animBottomSheet?.apply {
            speed = 0.8F
            scale = 0.7F
            setAnimation(R.raw.l_anim_photo_saving)
        }

        tvBottomSheet?.text = getDownloadingWithQualityMsg(requireContext())

        btnDismiss?.setOnClickListener{
            animBottomSheet?.apply {
                pauseAnimation()
                setAnimation(R.raw.l_anim_photo_saving)
            }
            dismiss()
        }

        PushDownAnim.setPushDownAnimTo(btnCancel)?.setOnClickListener {
            onCancel?.invoke()
        }
    }

    fun downloadStarted() {

        btnCancel?.showView()

        animBottomSheet?.apply {
            pauseAnimation()
            speed = 0.8F
            scale = 0.7F
            setAnimation(R.raw.l_anim_photo_saving)
            playAnimation()
        }

        if(!isAdded) {
            isCancelable = false
            showNow(fragManager, TAG)
        }
    }

    fun downloadError(msg: String = ""){

        downloadStarted()

        btnCancel?.hideView()

        val finalMsg = when {
            msg == NO_INTERNET -> {
                getString(R.string.error_no_internet)
            }
            msg.isNotEmpty() -> {
                msg
            }
            else -> getString(R.string.something_wrong_msg)
        }

        if(isAdded) {
            isDownloading = false
            isCancelable = true
            animBottomSheet?.apply {
                pauseAnimation()
                setAnimation(R.raw.l_anim_error_lochness_monster)
                playAnimation()
            }
            tvBottomSheet?.text = finalMsg
            btnDismiss?.showView()
        } else {
            showToast(finalMsg)
        }
    }

    fun downloaded(){
        CoroutineScope(Dispatchers.Main).launch {
            if(isAdded) {
                btnCancel?.hideView()
                animBottomSheet?.pauseAnimation()
                delay(300)
                animBottomSheet?.apply {
                    pauseAnimation()
                    setAnimation(R.raw.l_anim_photo_saving)
                }
                dismissAllowingStateLoss()
            }
        }
    }

    fun onProgress(progress: String) {
        btnCancel?.showView()
        try {
            tvBottomSheet?.text = progress
        } catch (e: Exception) {
            Log.e(TAG, "onProgress: $e")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    companion object {

        const val TAG = "BottomSheetDownload"

        fun getDownloadingWithQualityMsg(context: Context?) = context?.let { ctx ->
            val quality = MausamSharedPrefs(ctx).photoDownloadQuality ?: DownloadQuality.FULL
            "\n${ctx.getString(R.string.downloading, quality.text)}"
        } ?: "Downloading..."

    }

}