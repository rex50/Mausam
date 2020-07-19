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

        tvBottomSheet?.text = getString(R.string.downloading)

        btnDismiss?.setOnClickListener{
            animBottomSheet?.apply {
                pauseAnimation()
                setAnimation(R.raw.l_anim_photo_saving)
            }
            dismiss()
        }
    }

    fun downloadStarted(childFragmentManager: FragmentManager) {
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
        animBottomSheet?.pauseAnimation()
        fragmentManager?.apply {
            dismiss()
        }
    }

}