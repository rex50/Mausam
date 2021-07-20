package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.utils.Constants.Network.NO_INTERNET
import com.rex50.mausam.utils.showToast
import com.rex50.mausam.utils.showView
import kotlinx.android.synthetic.main.bs_download.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BSDownload(private var fragManager: FragmentManager) : MaterialBottomSheet() {

    companion object{
        const val TAG = "BottomSheetDownload"
    }

    var isDownloading = false

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

        "\n${getString(R.string.downloading)}".let { tvBottomSheet?.text = it }

        btnDismiss?.setOnClickListener{
            animBottomSheet?.apply {
                pauseAnimation()
                setAnimation(R.raw.l_anim_photo_saving)
            }
            dismiss()
        }
    }

    fun downloadStarted() {
        if(!isAdded) {
            isCancelable = false
            showNow(fragManager, TAG)
        }
    }

    fun downloadError(msg: String = ""){

        downloadStarted()

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
            animBottomSheet?.pauseAnimation()
            delay(300)
            fragmentManager?.apply {
                dismissAllowingStateLoss()
            }
        }
    }

    fun onProgress(progress: String) {
        try {
            tvBottomSheet?.text = progress
        } catch (e: Exception) {
            Log.e(TAG, "onProgress: $e")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}