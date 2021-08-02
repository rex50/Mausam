package com.rex50.mausam.views.fragments

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rex50.imageblur.ImageBlur
import com.rex50.mausam.BuildConfig
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.frag_blur_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class FragBlurImage: BaseFragment() {

    private var listener: OnBlurFragmentInteractions? = null

    private var blurLevel: Float = 0F
        set(value) {
            field = when {
                value > 25 -> 25F
                value < 0 -> 0F
                else -> value
            }
        }

    private val blurHelper: ImageBlur by lazy {
        ImageBlur.with(requireContext()).also {
            it.load(listener?.getBitmap())
        }
    }

    override fun getResourceLayout(): Int = R.layout.frag_blur_image

    override fun initView() {

        val displayMetrics = resources.displayMetrics

        sBlur?.max = 100

        //Set click listener to listen to the blur seekbar
        sBlur?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blurLevel = (progress.toFloat() / 4) //Divide to make it no more than 25
                updatePreview()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        //Set the ratio of height and width according to the display size
        clBlurRoot?.setRatioOfChild(R.id.cardPreview, "${displayMetrics.widthPixels}*${displayMetrics.heightPixels}")

        //Onclick of btnSetWallpaper
        PushDownAnim.setPushDownAnimTo(btnSetWallpaper, btnCropAgain).setOnClickListener {
            if (listener?.allowInteraction() == true) {
                ivLoader?.hideView()
                when(it.id) {
                    R.id.btnSetWallpaper -> CoroutineScope(Dispatchers.Main).launch {
                        var finalBitmap: Bitmap? = null
                        try {
                            finalBitmap =
                                if(blurLevel > 0 && listener?.getBitmap() != null)
                                    blurHelper.intensity(blurLevel)
                                        .onOutOfMemory {
                                            showToast(
                                            "Error while blurring image. Please try clearing memory before trying again.",
                                            Toast.LENGTH_LONG
                                            )
                                        }
                                        .getBlurredImageAsync()
                                else
                                    listener?.getBitmap()

                        } catch (e: Exception) {
                            Log.e(TAG, "initView: ", e)
                        }
                        finalBitmap?.let { bitmap ->
                            setWallpaper(bitmap)
                        } ?: showToast(getString(R.string.wallpaper_set_failed))
                    }

                    R.id.btnCropAgain -> {
                        listener?.cropAgain()
                    }
                }
            }
        }
    }

    override fun load() {
        initPreview()
    }

    override fun whenResumed() {
        initPreview()
    }

    /**
     * init preview with default values
     */
    private fun initPreview() {
        ivLoader?.hideView()
        sBlur?.progress = 0
        listener?.getBitmap()?.let {
            blurHelper.load(it)
            Glide.with(requireContext())
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivPreview)
        }
    }

    /**
     * Displays preview of the blurred image with the given intensity
     */
    private fun updatePreview() {
        listener?.getBitmap()?.takeIf { blurLevel > 0 }?.let {
            ImageBlur.with(requireContext())
                .load(it)
                .intensity(blurLevel)
                .into(ivPreview)
        } ?: initPreview()
    }

    /**
     * Start setting wallpaper process and shows appropriate message based on status
     */
    private suspend fun setWallpaper(bitmap: Bitmap) = withContext(Dispatchers.Main) {
        ivLoader?.showView()
        showToast("Setting Wallpaper...")
        listener?.let { li ->
            //Set Wallpaper
            li.onSetAsWallpaper(bitmap).let { isSuccess ->
                //Hide loader and show toast based on status
                ivLoader?.hideView()
                showToast(
                    if(isSuccess) getString(R.string.wallpaper_set_success)
                    else getString(R.string.wallpaper_set_failed)
                )

                if(isSuccess)
                    listener?.onWallpaperSetSuccess()
            }
        } ?: showToast(getString(R.string.wallpaper_set_failed))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnBlurFragmentInteractions)
            listener = context
        else if(BuildConfig.DEBUG)
            throw RuntimeException("$context should implement OnBlurFragmentInteractions")
    }

    companion object {
        const val TAG = "FragBlurImage"

        fun newInstance() = FragBlurImage()
    }

    interface OnBlurFragmentInteractions {

        /**
         * For getting bitmap
         *
         * @return cropped/optimized bitmap
         */
        fun getBitmap(): Bitmap?

        /**
         * For setting wallpaper
         *
         * @param bitmap which will be used to set as wallpaper
         * @return true if wallpaper was set successfully or false
         */
        suspend fun onSetAsWallpaper(bitmap: Bitmap): Boolean

        /**
         * For Navigating to the Image Cropping screen
         */
        fun cropAgain()

        /**
         * To check if any process is running or not(like setting wallpaper)
         * for avoiding user interaction during any running process
         *
         * @return true if user interaction allowed else false
         */
        fun allowInteraction(): Boolean

        /**
         * Called when all the process of setting wallpaper is completed
         */
        fun onWallpaperSetSuccess()

    }
}