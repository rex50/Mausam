package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.utils.showView
import kotlinx.android.synthetic.main.bs_download.*


class BSDownload : MaterialBottomSheet() {

    companion object{
        const val TAG = "BottomSheetDownload"
    }

    var isDownloading = false

    override fun layoutId(): Int = R.layout.bs_download

    override fun setupBehaviour(bottomSheetBehavior: BottomSheetBehavior<*>?) {
        bottomSheetBehavior?.apply {
            //peekHeight = 500
            //state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        isDownloading = true
        animBottomSheet?.speed = 0.8F
        animBottomSheet?.apply {
            setAnimation(R.raw.preloader_free_time)
        }

        tvBottomSheet?.text = getString(R.string.downloading)

        btnDismiss?.setOnClickListener{
            animBottomSheet?.apply {
                pauseAnimation()
                setAnimation(R.raw.preloader_free_time)
            }
            dismiss()
        }
    }

    fun downloadStarted(childFragmentManager: FragmentManager) {
        show(childFragmentManager, TAG)
    }

    fun downloadError(){
        isDownloading = false
        isCancelable = true
        animBottomSheet?.apply {
            pauseAnimation()
            setAnimation(R.raw.error_lochness_monster)
            playAnimation()
        }
        tvBottomSheet?.text = getString(R.string.failed_to_download_no_internet)
        btnDismiss?.showView()
    }

    fun downloaded(){
        animBottomSheet?.pauseAnimation()
        fragmentManager?.apply {
            dismiss()
        }
    }

}