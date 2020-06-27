package com.rex50.mausam.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.rex50.mausam.R
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager

class MaterialSnackBar(private val context: Context, private val layout: View) {
    private var snackBar: Snackbar? = null
    private val snackProgressBarManager: SnackProgressBarManager? = SnackProgressBarManager(layout, context.lifecycleOwner())
    private var snackProgressBar: SnackProgressBar? = null

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    private annotation class MaterialSnackBarDuration

    init {
        snackProgressBarManager?.apply {
            useRoundedCornerBackground(true)
            setBackgroundColor(R.color.colorAccent)
            setActionTextColor(R.color.colorAccentDark)
            setProgressBarColor(R.color.white)
            setOverlayLayoutColor(R.color.white_to_dark)
        }
    }

    fun showIndeterminateBar(@StringRes msg: Int){
        snackProgressBar = SnackProgressBar(SnackProgressBar.TYPE_HORIZONTAL, context.getString(msg))
        snackProgressBar?.apply {
            setSwipeToDismiss(false)
            setAllowUserInput(false)
            setIsIndeterminate(true)
        }
        snackProgressBar?.apply {
            snackProgressBarManager?.show(this, SnackProgressBarManager.LENGTH_INDEFINITE)
        }
    }

    fun show(@StringRes msg: Int, @MaterialSnackBarDuration duration: Int) {
        show(context.getString(msg), duration, null)
    }

    @JvmOverloads
    fun show(msg: String, @MaterialSnackBarDuration duration: Int, background: Drawable? = null) {
        if (isSupported) {
            snackBar = Snackbar.make(layout, msg, duration);
            showSnackBarWithMargin(snackBar, background)
        } else {
            showToast(msg, duration)
        }
    }

    fun showActionSnackBar(@StringRes msg: Int, @StringRes action: Int, @MaterialSnackBarDuration duration: Int, listener: SnackBarListener) {
        showActionSnackBar(context.getString(msg), context.getString(action), null, duration, listener)
    }

    fun showActionSnackBar(msg: String, action: String?, @MaterialSnackBarDuration duration: Int, listener: SnackBarListener) {
        showActionSnackBar(msg, action, null, duration, listener)
    }

    fun showActionSnackBar(msg: String, action: String?, background: Drawable?, @MaterialSnackBarDuration duration: Int, listener: SnackBarListener) {
        if (isSupported) {
            snackBar = Snackbar.make(layout, msg, duration)
                    .setAction(action) { view: View? -> listener.onActionPressed() }
            showSnackBarWithMargin(snackBar, background)
        } else {
            showToast(msg, duration)
        }
    }

    fun dismiss() {
        snackBar?.dismiss()
        snackBar = null
        snackProgressBarManager?.dismissAll()
    }

    private fun showSnackBarWithMargin(snackbar: Snackbar?, background: Drawable?) {
        val sideMargin = 0
        val marginBottom = 0
        val fontSize = 14f
        val textFont = Typeface.createFromAsset(context.assets, "fonts/Asap-SemiBold.ttf")
        val actionFont = Typeface.createFromAsset(context.assets, "fonts/Asap-Bold.ttf")
        val color = ResourcesCompat.getColor(context.resources, R.color.colorAccent, null)
        val colorDark = ResourcesCompat.getColor(context.resources, R.color.white, null)
        val snackBarView = snackbar!!.view
        //        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setMaxLines(2);

        //setting custom margins
        val params = snackBarView.layoutParams as MarginLayoutParams
        params.setMargins(params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + marginBottom)
        snackBarView.layoutParams = params
        if (background == null) {
            val radius = 8
            val drawable = GradientDrawable()
            drawable.setColor(color)
            drawable.cornerRadius = radius.toFloat()
            snackBarView.background = drawable
        } else snackBarView.background = background


        // change snackbar text color
        /*int snackbarTextId = android.support.design.R.id.snackbar_text;*/  // for support library
        val snackbarTextId = com.google.android.material.R.id.snackbar_text //for androidx
        val snackbarActionId = com.google.android.material.R.id.snackbar_action
        var textView = snackBarView.findViewById<TextView>(snackbarTextId)
        textView.typeface = textFont
        textView.setTextColor(colorDark)
        textView.textSize = fontSize
        textView.maxLines = 5

        //custom action text color
        textView = snackBarView.findViewById(snackbarActionId)
        textView.typeface = actionFont
        textView.setTextColor(colorDark)
        //        snackbar.setActionTextColor(colorDark);
        val baseTransientBottomBar: BaseTransientBottomBar<Snackbar>? = snackbar
        baseTransientBottomBar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        baseTransientBottomBar.show()

        //snackbar.show();
    }

    private val isSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    private fun showToast(msg: String, @MaterialSnackBarDuration duration: Int) {
        Toast.makeText(context, msg, if (LENGTH_SHORT == duration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
    }

    interface SnackBarListener {
        fun onActionPressed()
    }

    companion object {
        const val LENGTH_INDEFINITE = -2
        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0
    }

}