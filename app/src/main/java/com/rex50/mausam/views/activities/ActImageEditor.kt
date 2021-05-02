package com.rex50.mausam.views.activities

import android.app.WallpaperManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.canhub.cropper.CropImageView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import id.zelory.compressor.loadBitmap
import kotlinx.android.synthetic.main.act_image_editor.*
import kotlinx.coroutines.*
import java.io.IOException

class ActImageEditor : BaseActivity() {

    companion object {
        const val TAG = "ActImageEditor"
    }

    private var photoData: SavedImageMeta? = null

    private var progressBar: CircularProgressDrawable? = null

    override val layoutResource: Int
        get() = R.layout.act_image_editor

    override fun loadAct(savedInstanceState: Bundle?) {
        getIntentData()

        sBlur?.hideView()
        tvBlur?.hideView()

        photoData?.let {

            showLoader(true)

            CoroutineScope(Dispatchers.Main).launch {
                Log.d(TAG, "loadAct: ${photoData?.relativePath?.toUri()}")
                photoData?.relativePath?.toUri()?.getBitmap()?.let {
                    pvPreview?.apply {
                        resources?.displayMetrics?.let { metrics ->
                            //TODO: clParent?.setRatioOfChild(id, "${metrics.widthPixels}x${metrics.heightPixels}")
                            setAspectRatio(metrics.widthPixels, metrics.heightPixels)
                        }
                        setImageBitmap(it)
                        scaleType = CropImageView.ScaleType.FIT_CENTER
                        cropRect = wholeImageRect
                    }
                    showLoader(false)
                }
            }

            btnSetWallpaper?.setOnClickListener {
                showLoader(true)
                CoroutineScope(Dispatchers.IO).launch {
                    pvPreview?.croppedImage?.let {
                        setBitmapAsWallpaper(it)
                    }
                }
            }

        } ?: let {
            showToast(getString(R.string.something_wrong_msg))
            finish()
        }

    }

    private fun setBitmapAsWallpaper(bitmap: Bitmap) = runBlocking {
        val msg = try {
            WallpaperManager.getInstance(this@ActImageEditor).setBitmap(bitmap)
            finish()
            "Wallpaper set successfully"
        }catch (e: IOException) {
            "Error while setting Wallpaper"
        }

        withContext(Dispatchers.Main) {
            showToast(msg)
            showLoader(false)
        }
    }

    private fun getIntentData() {
        photoData = intent?.getSerializableExtra(PHOTO_DATA) as SavedImageMeta?
    }

    private fun showLoader(show: Boolean) {
        if(progressBar == null) {
            progressBar = CircularProgressDrawable(this).apply {
                strokeWidth = 6f
                centerRadius = 30f
                setColorSchemeColors(ContextCompat.getColor(this@ActImageEditor, R.color.black_to_white))
                start()
            }
            ivLoader?.setImageDrawable(progressBar)
        }
        ivLoader?.visibility(show)
    }

    override fun internetStatus(internetType: Int) {
        //TODO("Not yet implemented")
    }
}