package com.rex50.mausam.base_classes

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rex50.mausam.R

abstract class MaterialBottomSheet : BottomSheetDialogFragment() {

    private var customView: View? = null

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        customView = View.inflate(context, layoutId(), null)
        customView?.apply {
            dialog.setContentView(this)
        }
        bottomSheetBehavior = BottomSheetBehavior.from(customView?.parent as View)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        customView = inflater.inflate(layoutId(), container, false)
        return customView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady(view, savedInstanceState)
    }

    abstract fun layoutId(): Int

    abstract fun onViewReady(view: View, savedInstanceState: Bundle?)

    open fun setupBehaviour(bottomSheetBehavior: BottomSheetBehavior<*>?){}


    override fun onStart() {
        super.onStart()
        bottomSheetBehavior?.apply {
            isHideable = isCancelable
            isDraggable = isCancelable
            setupBehaviour(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customView = null
    }

}