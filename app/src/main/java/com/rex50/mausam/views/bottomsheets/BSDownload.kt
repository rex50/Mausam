package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.utils.showView
import kotlinx.android.synthetic.main.bs_download.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BSDownload : MaterialBottomSheet() {

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

    fun downloadStarted(childFragmentManager: FragmentManager) {
        if(isAdded) {
            dismissAllowingStateLoss()
        }
        isCancelable = false
        show(childFragmentManager, TAG)
    }

    fun downloadError(){
        isDownloading = false
        isCancelable = true
        animBottomSheet?.apply {
            pauseAnimation()
            setAnimation(R.raw.l_anim_error_lochness_monster)
            playAnimation()
        }
        tvBottomSheet?.text = getString(R.string.failed_to_download_no_internet)
        btnDismiss?.showView()
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

    fun onProgress(progress: Int) {
        try {
            "$progress%\n${getString(R.string.downloading)}".let { tvBottomSheet?.text = it }
        } catch (e: Exception) {
            Log.e(TAG, "onProgress: $e")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}