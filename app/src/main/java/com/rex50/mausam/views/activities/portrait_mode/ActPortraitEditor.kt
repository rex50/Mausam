package com.rex50.mausam.views.activities.portrait_mode

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.util.Size
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.base_classes.VisionImageProcessor
import com.rex50.mausam.databinding.ActPortraitModeBinding
import com.rex50.mausam.utils.BitmapUtils
import com.rex50.mausam.utils.DrawingOverlay
import com.rex50.mausam.utils.FrameAnalyser
import com.rex50.mausam.utils.segmenter.GraphicOverlay
import com.rex50.mausam.utils.segmenter.SegmenterProcessor
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class ActPortraitEditor: BaseActivityWithBinding<ActPortraitModeBinding>() {

    private lateinit var previewView : PreviewView
    private lateinit var cameraProviderListenableFuture : ListenableFuture<ProcessCameraProvider>

    private lateinit var drawingOverlay: DrawingOverlay

    private lateinit var frameAnalyser : FrameAnalyser



    //New for Still image
    private val selectedSize: String = SIZE_SCREEN

    // Max width (portrait mode)
    private var imageMaxWidth = 0
    // Max height (portrait mode)
    private var imageMaxHeight = 0
    private var imageProcessor: VisionImageProcessor? = null

    private var imageUri: Uri? = null

    private var isLandScape = false

    override fun bindView(): ActPortraitModeBinding {
        return ActPortraitModeBinding.inflate(layoutInflater)
    }

    override fun loadAct(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        previewView = binding?.cameraPreviewView!!
        drawingOverlay = binding?.cameraDrawingOverlay!!
        drawingOverlay.setWillNotDraw(false)
        drawingOverlay.setZOrderOnTop(true)

        frameAnalyser = FrameAnalyser( drawingOverlay )

        createImageProcessor()

        startChooseImageIntentForResult()

        val rootView = findViewById<View>(R.id.root)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    imageMaxWidth = rootView.width
                    imageMaxHeight = rootView.height

                    tryReloadAndDetectInImage()
                }
            })

    }

    private fun onImagePicked() {
        if (ActivityCompat.checkSelfPermission( this , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
        else {
            // Permission granted, start the camera live feed
            //setupCameraProvider()
            tryReloadAndDetectInImage()
        }
    }

    private fun createImageProcessor() {
        imageProcessor = SegmenterProcessor(this, isStreamMode = false)
    }


    private fun tryReloadAndDetectInImage() {
        try {
            if (imageUri == null) {
                return
            }

            if (/*SIZE_SCREEN == selectedSize && */imageMaxWidth == 0) {
                // UI layout has not finished yet, will reload once it's ready.
                return
            }

            val imageBitmap = BitmapUtils.getBitmapFromContentUri(contentResolver, imageUri)
                ?: return
            // Clear the overlay first
            binding?.graphicOverlay?.clear()

            val resizedBitmap: Bitmap = if (selectedSize == SIZE_ORIGINAL) {
                imageBitmap
            } else {
                // Get the dimensions of the image view
                val targetedSize: Pair<Int, Int> = targetedWidthHeight

                // Determine how much to scale down the image
                val scaleFactor = Math.max(
                    imageBitmap.width.toFloat() / targetedSize.first.toFloat(),
                    imageBitmap.height.toFloat() / targetedSize.second.toFloat()
                )
                Bitmap.createScaledBitmap(
                    imageBitmap,
                    (imageBitmap.width / scaleFactor).toInt(),
                    (imageBitmap.height / scaleFactor).toInt(),
                    true
                )
            }

            binding?.preview?.setImageBitmap(resizedBitmap)
            if (imageProcessor != null) {
                binding?.graphicOverlay?.setImageSourceInfo(
                    resizedBitmap.width, resizedBitmap.height, /* isFlipped= */false
                )
                imageProcessor!!.processBitmap(resizedBitmap, binding?.graphicOverlay)
            } else {
                Log.e(
                    TAG,
                    "Null imageProcessor, please check adb logs for imageProcessor creation error"
                )
            }
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error retrieving saved image"
            )
            imageUri = null
        }
    }

    private val targetedWidthHeight: Pair<Int, Int>
        get() {
            val targetWidth: Int
            val targetHeight: Int
            when (selectedSize) {
                SIZE_SCREEN -> {
                    targetWidth = imageMaxWidth
                    targetHeight = imageMaxHeight
                }
                else -> throw IllegalStateException("Unknown size")
            }
            return Pair(targetWidth, targetHeight)
        }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CHOOSE_IMAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data!!.data
            onImagePicked()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun setupCameraProvider() {
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance( this )
        cameraProviderListenableFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderListenableFuture.get()
                bindPreview(cameraProvider)
            }
            catch (e: ExecutionException) {
                Log.e("APP", e.message!!)
            }
            catch (e: InterruptedException) {
                Log.e("APP", e.message!!)
            }
        }, ContextCompat.getMainExecutor( this ))
    }


    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch( Manifest.permission.CAMERA )
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission() ) {
            isGranted : Boolean ->
        if ( isGranted ) {
            //setupCameraProvider()
            tryReloadAndDetectInImage()
        }
        else {
            val alertDialog = AlertDialog.Builder( this ).apply {
                setTitle( "Permissions" )
                setMessage( "The app requires the camera permission to function." )
                setPositiveButton( "GRANT") { dialog, _ ->
                    dialog.dismiss()
                    requestCameraPermission()
                }
                setNegativeButton( "CLOSE" ) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                setCancelable( false )
                create()
            }
            alertDialog.show()
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val displayMetrics = resources.displayMetrics
        val screenSize = Size( displayMetrics.widthPixels, displayMetrics.heightPixels)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution( screenSize )
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector,
            preview
        )

        // Attach the frameAnalyser here ...
        imageAnalysis.setAnalyzer( Executors.newSingleThreadExecutor() , frameAnalyser )
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

    companion object {
        const val TAG = "ActPortraitMode"

        const val REQUEST_CHOOSE_IMAGE = 101

        const val SIZE_SCREEN = "w:screenSize"
        const val SIZE_ORIGINAL = "w:original"
    }
}