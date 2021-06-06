package com.rex50.mausam.views.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.canhub.cropper.CropImageView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import com.rex50.mausam.views.activities.ActImageEditor
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.frag_crop_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragCropImage : BaseFragment() {

    private var listener: OnCropFragmentInteractionListener? = null

    private var photoData: SavedImageMeta? = null

    private var progressBar: CircularProgressDrawable? = null

    private var displayMetrics: Pair<Int, Int> = Pair(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoData = it.getSerializable(PHOTO_DATA) as SavedImageMeta?
        }
    }

    override fun getResourceLayout(): Int = R.layout.frag_crop_image

    override fun initView() {

    }

    override fun load() {
        btnCropImage?.hideView()
        photoData?.let {

            showLoader(true)

            CoroutineScope(Dispatchers.Main).launch {
                Log.d(ActImageEditor.TAG, "loadAct: ${photoData?.relativePath?.toUri()}")
                photoData?.relativePath?.toUri()?.let { uri ->

                    uri.getOptimizedBitmap(requireContext())?.let { image ->
                        pvPreview?.apply {
                            resources?.displayMetrics?.let { metrics ->
                                displayMetrics = Pair(metrics.widthPixels, metrics.heightPixels)
                            }
                            setAspectRatio(displayMetrics.first, displayMetrics.second)
                            try {
                                setImageBitmap(image)
                            } catch (e: Exception) {
                                Log.e(TAG, "load: ", e)
                            }
                            scaleType = CropImageView.ScaleType.FIT_CENTER
                            cropRect = wholeImageRect
                        }

                    }

                }

                showLoader(false)
                btnCropImage?.showView()
            }

            PushDownAnim.setPushDownAnimTo(btnCropImage).setOnClickListener {
                showLoader(true)
                CoroutineScope(Dispatchers.IO).launch {
                    //Crop Image according to screen height and width
                    pvPreview?.getCroppedImage(displayMetrics.first, displayMetrics.second)?.let {
                        withContext(Dispatchers.Main) {
                            listener?.onCropSuccess(it)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        showLoader(false)
                    }
                }
            }

        } ?: showToast(getString(R.string.something_wrong_msg))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnCropFragmentInteractionListener)
            listener = context
    }

    fun showLoader(show: Boolean) {
        if(progressBar == null) {
            progressBar = CircularProgressDrawable(requireContext()).apply {
                strokeWidth = 6f
                centerRadius = 30f
                setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.black_to_white))
                start()
            }
            ivLoader?.setImageDrawable(progressBar)
        }
        ivLoader?.visibility(show)
    }


    companion object {

        const val TAG = "FragCropImage"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param photoData Information of the image.
         * @return A new instance of fragment FragCropImage.
         */
        fun newInstance(photoData: SavedImageMeta) =
                FragCropImage().apply {
                    arguments = Bundle().apply {
                        putSerializable(PHOTO_DATA, photoData)
                    }
                }
    }

    interface OnCropFragmentInteractionListener {

        /**
         * Called when cropping is completed successfully
         *
         * @param bitmap : cropped bitmap
         */
        fun onCropSuccess(bitmap: Bitmap)

    }
}