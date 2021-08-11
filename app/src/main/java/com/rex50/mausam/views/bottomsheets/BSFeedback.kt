package com.rex50.mausam.views.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.network.APIManager
import com.rex50.mausam.network.Result
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.hideKeyboard
import com.rex50.mausam.utils.showKeyBoard
import com.rex50.mausam.utils.showToast
import kotlinx.android.synthetic.main.bs_feedback.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BSFeedback : MaterialBottomSheet(){

    companion object{
        const val TAG = "BSUserMore"
    }

    private val apiManager: APIManager? by lazy {
        APIManager.getInstance(requireContext())
    }

    private val mausamSharedPrefs: MausamSharedPrefs? by lazy {
        MausamSharedPrefs(requireContext())
    }

    private var isSubmitted = false

    override fun layoutId(): Int = R.layout.bs_feedback

    fun show(fragmentManager: FragmentManager): BSFeedback{
        show(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        //show drafted feedback
        etFeedback?.setText(mausamSharedPrefs?.draftFeedbackMsg)

        focusFeedbackEditText()

        btnSubmitFeedback?.setOnClickListener { v ->
            val feedback = etFeedback?.text?.toString()
            if(feedback?.isNotEmpty() == true) {
                showToast("Submitting...")
                submitFeedback(feedback)
            } else {
                focusFeedbackEditText()
                showToast("Please enter some feedback before submitting")
            }
        }
    }

    private fun submitFeedback(feedback: String) {
        submitting(true)

        //Record feedback in google sheet
        CoroutineScope(Dispatchers.Main).launch {
            when(apiManager?.makeFeedbackRequest(feedback)) {
                is Result.Success -> {
                    isSubmitted = true
                    showToast("Feedback submitted successfully.")
                    dismissAllowingStateLoss()
                }

                is Result.Failure -> {
                    showToast("Unable to submit feedback. Please try again later.")
                    submitting(false)
                }
            }
        }
    }

    private fun focusFeedbackEditText() {
        etFeedback?.requestFocus()
        context?.showKeyBoard(etFeedback)
    }

    private fun submitting(state: Boolean) {
        btnSubmitFeedback?.isClickable = !state
        etFeedback?.isEnabled = !state
    }

    override fun onDismiss(dialog: DialogInterface) {

        etFeedback?.isEnabled = false
        context?.hideKeyboard(etFeedback)

        mausamSharedPrefs?.draftFeedbackMsg = if(!isSubmitted) {
            val draft = etFeedback?.text?.trim()?.toString() ?: ""
            if (draft.isNotEmpty())
                showToast("Saved feedback as draft")
            draft
        } else
            ""

        super.onDismiss(dialog)
    }

}