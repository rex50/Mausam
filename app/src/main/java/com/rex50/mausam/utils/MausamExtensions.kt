package com.rex50.mausam.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rex50.mausam.R
import com.rex50.mausam.utils.Constants.Configs.MAX_BITMAP_SIZE
import id.zelory.compressor.Compressor
import id.zelory.compressor.calculateInSampleSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun String.isInt(): Boolean = when(toIntOrNull()){
    null -> false
    else -> true
}

fun String.toBoolean(): Boolean = equals("true")

fun Boolean.getString(): String = if(this) "true" else "false"

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

fun View.visibility(show: Boolean) {
    if(show) showView() else hideView()
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

    val progressBar = CircularProgressDrawable(context)
    progressBar.apply {
        strokeWidth = 6f
        centerRadius = 30f
        setColorSchemeColors(ContextCompat.getColor(context, R.color.black_to_white))
        start()
    }

    Glide.with(context)
            .load(url) //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
            .placeholder(progressBar)
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

fun Context.isLocationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun Context.isStoragePermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED



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

fun checkIfFileExistsInStorage(relativePath: String?): Boolean {
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val retCol = arrayOf(MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.RELATIVE_PATH)
        var cId: String?
        imageMeta?.getUri().toString().split("/").apply {
            cId = this?.get(lastIndex)
        }
        context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                retCol,
                MediaStore.MediaColumns._ID + "='" + cId + "'", null, null
        )?.use { cur ->
            if (cur.count == 0) {
                return false
            }
            cur.moveToFirst()
            val id = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.TITLE))
            id?.takeIf { isNotEmpty() }?.apply {
                return true
            }
        }
    }else{*/
    val file: File? = File(relativePath ?: "")
    file?.apply {
        if(this.exists())
            return true
    }
    //}
    return false
}

suspend fun Uri.getBitmap(): Bitmap? {
    val file = File(path ?: "")
    return try {
        withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(file.path)
        }
    } catch (e: Exception) {
        Log.e("Uri Extension", "getBitmap: ", e)
        null
    }
}

suspend fun Uri.asCompressedBitmap(context: Context?) : Bitmap? {
    context?.let { con ->

        //below block will return a compressed and scaled down version of the image

        //Currently returning only compressed image. For scaling uncomment below code
        val file = File(path ?: "")

        try {

            //First try just compressing the image to reduce the image size
            val compressed = Compressor.compress(con, file)
            return BitmapFactory.decodeFile(compressed.path)

        } catch (e: Exception) {

            //If some exception occurs then try rescaling the image
            return BitmapFactory.Options().run {
                //If required scale down the image. For that first we will get image details
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.path)
                inSampleSize = calculateInSampleSize(this, 720, 1280)
                inJustDecodeBounds = false

                //finally return the image
                BitmapFactory.decodeFile(file.path)
            }
        }

        /*return BitmapFactory.Options().run {

            //First compress the image to reduce the image
            val compressedFile = Compressor.compress(con, File(path ?: ""))

            //If required scale down the image. For that first we will get image details
            *//*inJustDecodeBounds = true
            BitmapFactory.decodeFile(compressedFile.path)
            inSampleSize = calculateInSampleSize(this, 720, 1280)
            inJustDecodeBounds = false*//*

            //finally return the image
            BitmapFactory.decodeFile(compressedFile.path)
        }*/
    }

    //if context is null the result will be compressed
    return null
}


/**
 * Use this function to get a compressed version of Bitmap
 *
 * @param context of the current Activity or fragment
 * @return Compressed bitmap which can be drawn
 */
suspend fun Uri.getOptimizedBitmap(context: Context?): Bitmap? {
    var bitmap: Bitmap? = getBitmap()
    var file = File(path ?: "")
    withContext(Dispatchers.IO) {
        context?.let {
            while ((bitmap?.byteCount?:0) > MAX_BITMAP_SIZE) {
                Log.d("Bitmap", "getOptimizedBitmap: Compressing bitmap of size ${bitmap?.byteCount}")
                file = Compressor.compress(context, file)
                bitmap = BitmapFactory.decodeFile(file.path)
            }
        }
    }
    return bitmap
}

fun ConstraintLayout.setRatioOfChild(@IdRes id: Int, ratio: String) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    constraintSet.setDimensionRatio(id, ratio.toRatio().addBefore("w, "))
    constraintSet.applyTo(this)
}

fun String.toRatio() = when {
    this.contains("*") -> this.replace("*", ":")
    this.contains("x") -> this.replace("x", ":")
    this.contains("X") -> this.replace("X", ":")
    else -> this
}

fun AppCompatActivity.getSimpleFragmentAdapter(mFragmentList: List<Fragment>): FragmentStatePagerAdapter? {
    return object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }
    }
}