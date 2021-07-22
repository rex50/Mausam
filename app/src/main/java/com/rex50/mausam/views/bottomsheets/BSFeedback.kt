package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.utils.showKeyBoard
import com.rex50.mausam.utils.showToast
import kotlinx.android.synthetic.main.bs_feedback.*

class BSFeedback : MaterialBottomSheet(){

    companion object{
        const val TAG = "BSUserMore"
    }

    override fun layoutId(): Int = R.layout.bs_feedback

    fun show(fragmentManager: FragmentManager): BSFeedback{
        show(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        btnSubmitFeedback?.setOnClickListener { v ->
            val feedback = etIssue?.text?.toString()
            if(feedback?.isNotEmpty() == true) {
                //TODO: Setup a google sheet and record responses there
                showToast("Feedback submitted successfully.")
                dismissAllowingStateLoss()
            } else {
                etIssue?.requestFocus()
                context?.showKeyBoard(etIssue)
                showToast("Please enter some feedback before submitting")
            }
        }
    }

}