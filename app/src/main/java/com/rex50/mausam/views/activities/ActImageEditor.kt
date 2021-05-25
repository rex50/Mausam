package com.rex50.mausam.views.activities

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import com.rex50.mausam.views.fragments.FragCropImage
import kotlinx.android.synthetic.main.act_image_editor.*
import kotlinx.coroutines.*
import java.io.IOException

class ActImageEditor : BaseActivity(), FragCropImage.OnCropFragmentInteractionListener {

    companion object {
        const val TAG = "ActImageEditor"
    }

    private var photoData: SavedImageMeta? = null

    private var fragCropImage: FragCropImage? = null

    override val layoutResource: Int
        get() = R.layout.act_image_editor

    override fun loadAct(savedInstanceState: Bundle?) {

        getIntentData()

        photoData?.let {

            //load Fragments
            prepareFragments(it)

        } ?: let {
            showToast(getString(R.string.something_wrong_msg))
            finish()
        }

    }

    /**
     * Use this method to prepare the list of Fragments for Image manipulations
     * and show them in the Viewpager
     *
     * @param photoData - Data of image which will be used for manipulation
     */
    private fun prepareFragments(photoData: SavedImageMeta) {
        val listOfFragments = arrayListOf<Fragment>().apply {
            add(FragCropImage.newInstance(photoData).also { fragCropImage = it })
        }
        vpEditor?.apply {
            setPagingEnabled(false)
            adapter = getSimpleFragmentAdapter(listOfFragments)
            offscreenPageLimit = listOfFragments.size
            if(listOfFragments.size > 1)
                pageIndicator?.setViewPager(this)
            else
                pageIndicator?.hideView()
        }
    }

    /**
     *  Call this method to set Wallpaper
     *
     *  @param bitmap - Bitmap which will be used to set wallpaper
     *  @return true if wallpaper is set successful else false
     */
    private suspend fun setBitmapAsWallpaper(bitmap: Bitmap): Boolean {
        val msg = try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "setBitmapAsWallpaper: Setting Wallpaper...")
                WallpaperManager.getInstance(this@ActImageEditor).setBitmap(bitmap)
            }
            finish()
            true
        }catch (e: IOException) {
            false
        }

        Log.d(TAG, "setBitmapAsWallpaper: Status: $msg")

        return msg
    }

    /**
     * For setting wallpaper asynchronously and
     *
     * @param bitmap - Bitmap which will be used to set wallpaper
     */
    private fun setWallpaperAsync(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            //TODO: show loader if possible as this can take more than 300ms
            val isSuccess = setBitmapAsWallpaper(bitmap)
            showToast(if(isSuccess) getString(R.string.wallpaper_set_success) else getString(R.string.wallpaper_set_failed))
        }
    }

    private fun getIntentData() {
        photoData = intent?.getSerializableExtra(PHOTO_DATA) as SavedImageMeta?
    }

    override fun onCropSuccess(bitmap: Bitmap) {
        //TODO: start editor with cropped bitmap as preview
    }


}