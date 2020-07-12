package com.rex50.mausam.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rex50.mausam.R
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

fun String.isInt(): Boolean = when(toIntOrNull()){
    null -> false
    else -> true
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View?) {
    val imm: InputMethodManager? = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Fragment.showKeyBoard(){
    view?.let { activity?.showKeyBoard(it) }
}

fun Activity.showKeyBoard() {
    showKeyBoard(currentFocus ?: View(this))
}

fun Context.showKeyBoard(view: View?) {
    view?.apply {
        val imm: InputMethodManager? = this@showKeyBoard?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}

fun Fragment.showToast(msg: String){
    context?.showToast(msg)
}

fun Activity.showToast(msg: String){
    toast(this, msg)
}

fun Context.showToast(msg: String){
    toast(this, msg)
}

private fun toast(context: Context?, msg: String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun View.toggleViewVisibility(){
    visibility = if(isVisible) GONE else VISIBLE
}

fun View.showView(){
    visibility = VISIBLE
}

fun View.hideView(){
    visibility = GONE
}

fun View.setEnabled(){
    isEnabled = true
}

fun View.setDisabled(){
    isEnabled = false
}

fun View?.isViewVisible(): Boolean = if(this == null) false else visibility == VISIBLE

fun String.toDateFormat(pattern: String) : String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSS", Locale.getDefault())
    val dateTime = DateTime(dateFormat.parse(this))
    return dateTime.toString(pattern)
}

fun String.addBefore(text: String?): String = text?.takeIf { it.isNotEmpty() }?.let {
    "$text $this"
}?: this

fun String.addAfter(text: String?): String = text?.takeIf { it.isNotEmpty() }?.let {
    "$this $text"
}?: this

fun ImageView.loadImage(url: String?){
    Glide.with(context)
            .load(url) //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this)
    //loadImageWithThumbnail(url, R.drawable.ic_loader)
}

fun ImageView.loadImageWithPreLoader(url: String?, @DrawableRes preLoader: Int? = R.drawable.ic_loader){
    Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
            .thumbnail(Glide.with(this).load(preLoader))
            //.transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
}

fun Context.openUrl(url: String){
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Context.openInstagramProfile(userName: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/$userName")).setPackage("com.instagram.android"))
    } catch (e: ActivityNotFoundException) {
        openUrl("http://instagram.com/$userName")
    }
}

fun Context.openTwitterProfile(twitterUsername: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=$twitterUsername")))
    } catch (e: ActivityNotFoundException) {
        openUrl("https://twitter.com/#!/$twitterUsername")
    }
}

fun String?.optString() : String =
        this ?: "null"

fun String?.getTextOrEmpty() : String =
        if (optString().equals("null", ignoreCase = true)) "" else this!!

fun Any?.isNull(): Boolean = this == null

fun Any?.isNotNull(): Boolean = this != null

fun Context.lifecycleOwner(): LifecycleOwner? {
    var curContext = this
    var maxDepth = 20
    while (maxDepth-- > 0 && this !is LifecycleOwner) {
        curContext = (this as ContextWrapper).baseContext
    }
    return if (curContext is LifecycleOwner) {
        curContext
    } else {
        null
    }
}

fun Context?.calculateDp(px: Int) : Int = calculateDp(this?.resources, px)
fun Fragment?.calculateDp(px: Int) : Int = calculateDp(this?.resources, px)
fun Activity?.calculateDp(px: Int) : Int = calculateDp(this?.resources, px)
fun calculateDp(r: Resources?, px: Int) : Int =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px.toFloat(),
                r?.displayMetrics
        ).toInt()

/**
 * Animations
 */

fun FloatingActionButton.toNormal(){
    layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).also {
        it.addRule(RelativeLayout.ALIGN_PARENT_START)
        it.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        it.setMargins(marginStart, marginTop, marginEnd, marginBottom)
    }
    rotation = 0F
}

fun FloatingActionButton.toRightAndRotate(){
    layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).also {
        it.addRule(RelativeLayout.ALIGN_PARENT_END)
        it.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        it.setMargins(marginStart, marginTop, marginEnd, marginBottom)
    }
    rotation = 90F
}