package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import kotlinx.android.synthetic.main.bs_delete_confirmation.*

class BSDeleteConfirmation : MaterialBottomSheet(){

    companion object{
        const val TAG = "BSUserMore"
    }

    var onDelete: ((BSDeleteConfirmation) -> Unit)? = null

    override fun layoutId(): Int = R.layout.bs_delete_confirmation

    fun show(fragmentManager: FragmentManager): BSDeleteConfirmation{
        show(fragmentManager, TAG)
        return this
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {

        tvError?.text = getString(R.string.msg_sure_delete_photo)

        btnDelete?.setOnClickListener {
            onDelete?.invoke(this)
        }
    }

}