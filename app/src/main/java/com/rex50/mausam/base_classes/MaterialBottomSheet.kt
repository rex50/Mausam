package com.rex50.mausam.base_classes

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rex50.mausam.R
import android.widget.FrameLayout
import android.graphics.Insets
import android.view.WindowInsets
import android.os.Build


abstract class MaterialBottomSheet(val showFullScreen: Boolean = false) : BottomSheetDialogFragment() {

    private var customView: View? = null

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        customView = View.inflate(context, layoutId(), null)
        customView?.apply {
            dialog.setContentView(this)
        }
        bottomSheetBehavior = BottomSheetBehavior.from(customView?.parent as View)
        if(showFullScreen) {
            dialog.setOnShowListener { dialogInterface ->
                val bottomSheetDialog =
                    dialogInterface as BottomSheetDialog
                setupFullHeight(bottomSheetDialog)
            }
        }
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

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

        bottomSheet?.let {
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
            val layoutParams = bottomSheet.layoutParams
            val windowHeight = getScreenHeight(requireActivity())
            if (layoutParams != null) {
                layoutParams.height = windowHeight
            }
            bottomSheet.layoutParams = layoutParams
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    open fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }


    override fun onStart() {
        super.onStart()
        bottomSheetBehavior?.apply {
            setupBehaviour(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customView = null
    }

}