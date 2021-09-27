package com.rex50.mausam.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions

class FrameAnalyser( private var drawingOverlay: DrawingOverlay) : ImageAnalysis.Analyzer {

    // Initialize Segmentor
    private val options =
        SelfieSegmenterOptions.Builder()
            .setDetectorMode( SelfieSegmenterOptions.STREAM_MODE )
            .build()
    private val segmenter = Segmentation.getClient(options)
    private var frameMediaImage : Image? = null

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        frameMediaImage = image.image
        if ( frameMediaImage != null) {
            val inputImage = InputImage.fromMediaImage( frameMediaImage , image.imageInfo.rotationDegrees )
            segmenter.process( inputImage )
                .addOnSuccessListener { segmentationMask ->
                    val mask = segmentationMask.buffer
                    val maskWidth = segmentationMask.width
                    val maskHeight = segmentationMask.height
                    mask.rewind()
                    val bitmap = Bitmap.createBitmap( maskWidth , maskHeight , Bitmap.Config.ARGB_8888 )
                    bitmap.copyPixelsFromBuffer( mask )

                    // Pass the segmentation bitmap to drawingOverlay
                    drawingOverlay.maskBitmap = bitmap
                    drawingOverlay.invalidate()
                }
                .addOnFailureListener { exception ->
                    Log.e( "App" , exception.message!! )
                }
                .addOnCompleteListener {
                    image.close()
                }
        }
    }
}