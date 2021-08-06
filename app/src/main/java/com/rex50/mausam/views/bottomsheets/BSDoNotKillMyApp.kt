package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import kotlinx.android.synthetic.main.bs_do_not_kill_my_app.*

class BSDoNotKillMyApp : MaterialBottomSheet(true){

    companion object{
        const val TAG = "BSUserMore"
    }

    override fun layoutId(): Int = R.layout.bs_do_not_kill_my_app

    fun show(fragmentManager: FragmentManager): BSDoNotKillMyApp{
        show(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        svContent?.let {
            ContextCompat.getColor(requireContext(), R.color.black_to_white).let { color ->
                it.primaryTextColor = color
                it.secondaryTextColor = color
                it.activeIconsColor = color
            }
            it.inactiveIconsColor = ContextCompat.getColor(requireContext(), R.color.graniteGray)
            ContextCompat.getColor(requireContext(), R.color.white_to_dark).let { color ->
                it.setBackgroundColor(color)
                it.dividerColor = color
            }
            it.setButtonsVisibility(false)
            it.setExplanationVisibility(false)
            it.setDeveloperSolutionVisibility(false)
            Handler(Looper.getMainLooper()).postDelayed({
                it.loadContent(appName = getString(R.string.app_name))
            }, 500)
        }

        btnDismiss?.setOnClickListener {
            dismiss()
        }
    }

}