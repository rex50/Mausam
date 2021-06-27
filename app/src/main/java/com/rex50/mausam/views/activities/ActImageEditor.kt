package com.rex50.mausam.views.activities

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import com.rex50.mausam.views.fragments.FragBlurImage
import com.rex50.mausam.views.fragments.FragCropImage
import kotlinx.android.synthetic.main.act_image_editor.*
import kotlinx.coroutines.*
import java.io.IOException

class  ActImageEditor : BaseActivity(), FragCropImage.OnCropFragmentInteractionListener,
    FragBlurImage.OnBlurFragmentInteractions {

    companion object {
        const val TAG = "ActImageEditor"

        const val INITIAL_PAGE = 0
    }

    private var photoData: UnsplashPhotos? = null

    private var fragCropImage: FragCropImage? = null

    private var croppedImage: Bitmap? = null

    private var isProcessing = false

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
    private fun prepareFragments(photoData: UnsplashPhotos) {
        val listOfFragments = arrayListOf<Fragment>().apply {
            add(FragCropImage.newInstance(photoData).also { fragCropImage = it })
            add(FragBlurImage.newInstance())
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
        isProcessing = true
        val msg = try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "setBitmapAsWallpaper: Setting Wallpaper...")
                WallpaperManager.getInstance(this@ActImageEditor).setBitmap(bitmap)
            }
            true
        }catch (e: IOException) {
            false
        }
        isProcessing = false

        Log.d(TAG, "setBitmapAsWallpaper: Status: $msg")

        return msg
    }

    private fun getIntentData() {
        photoData = intent?.getParcelableExtra(PHOTO_DATA) as UnsplashPhotos?
    }

    override fun onCropSuccess(bitmap: Bitmap) {
        //start editor with cropped bitmap as preview
        croppedImage = bitmap
        if((vpEditor?.childCount ?: 0) > 1)
            vpEditor?.currentItem = 1
    }

    override fun getBitmap(): Bitmap? {
        return croppedImage
    }

    override suspend fun onSetAsWallpaper(bitmap: Bitmap): Boolean {
        return setBitmapAsWallpaper(bitmap)
    }

    override fun cropAgain() {
        if((vpEditor?.childCount ?: 0) >= 1)
            vpEditor?.currentItem = 0
    }

    override fun allowInteraction(): Boolean {
        return !isProcessing
    }

    override fun onWallpaperSetSuccess() {
        if(!isFinishing && !isDestroyed)
            finish()
    }

    override fun onBackPressed() {
        when {

            //Check if current page is 1 then change page to initial page
            (vpEditor?.childCount ?: INITIAL_PAGE) > 1 && vpEditor?.currentItem == 1 -> {
                vpEditor?.currentItem = INITIAL_PAGE
            }

            else -> super.onBackPressed()
        }
    }


}