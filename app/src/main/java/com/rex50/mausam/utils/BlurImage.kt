package com.rex50.mausam.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.annotation.IntegerRes
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class BlurImage private constructor(private val context: Context) {

    var bitmapScale = 0.3f
        private set

    private var image: Bitmap? = null

    private var radius = 08f

    private var allowMultipleTask = false

    private var onOutOfMemory: (() -> Unit)? = null

    private val renderScript: RenderScript by lazy {
        RenderScript.create(context)
    }


    private val asyncScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + Job())
    }

    private var lastBlurJob: Job? = null


    /**
     * Scales and blurs a bitmap based on a given radius using {@link Renderscript}
     *
     * While performing this operation it can throw {@link OutOfMemoryError} which is
     * handled by the function and you can use {@link #onOutOfMemory} for a callback.
     *
     * @return blurred image if operation was successful else returns the original bitmap.
     *
     * Renderscript is going to be deprecated from Android 12 so need to update below code soon
     * to support Android 12 later using below library
     * @see: https://github.com/android/renderscript-intrinsics-replacement-toolkit
     */
    private fun blur(): Bitmap? {
        return try {
            image?.let { bitmap ->
                val width = (bitmap.width * bitmapScale).roundToInt()
                val height = (bitmap.height * bitmapScale).roundToInt()

                val input = Bitmap.createScaledBitmap(bitmap, width, height, false)
                val output = Bitmap.createBitmap(input)

                val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
                val inputAllocation = Allocation.createFromBitmap(renderScript, input)
                val outputAllocation = Allocation.createFromBitmap(renderScript, output)

                intrinsicBlur.setRadius(radius)
                intrinsicBlur.setInput(inputAllocation)
                intrinsicBlur.forEach(outputAllocation)
                outputAllocation.copyTo(output)

                output
            } ?: image
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "scale: ", e)
            onOutOfMemory?.invoke()
            image
        }
    }


    fun load(bitmap: Bitmap?): BlurImage {
        image = bitmap
        return this
    }

    fun load(@IntegerRes res: Int): BlurImage {
        image = BitmapFactory.decodeResource(context.resources, res)
        return this
    }

    fun radius(radius: Float): BlurImage {
        this.radius = if (radius < MAX_RADIUS && radius > 0) radius else MAX_RADIUS
        return this
    }

    fun allowMultipleTask(allow: Boolean = true): BlurImage {
        allowMultipleTask = allow
        return this
    }

    fun onOutOfMemory(callback: () -> Unit): BlurImage{
        onOutOfMemory = callback
        return this
    }

    fun into(imageView: ImageView?) {
        if(!allowMultipleTask && lastBlurJob?.isActive == true)
            lastBlurJob?.cancel(CancellationException("Trying a new image to blur"))

        lastBlurJob = asyncScope.launch {
            try {
                val bitmap = getBlurredImageAsync()
                imageView?.setImageBitmap(bitmap)
            } catch (e: CancellationException) {
                Log.w(TAG, "Cancelled last blur job and currently allowMultipleTask is $allowMultipleTask")
            }
        }
    }

    fun scale(scale: Float): BlurImage {
        bitmapScale = when {
            scale > 1.0f -> MAX_SCALE
            scale <= 0 -> MIN_SCALE
            else -> scale
        }
        return this
    }

    suspend fun getBlurredImageAsync(): Bitmap? = withContext(Dispatchers.IO) {
        return@withContext blur()
    }

    fun getBlurredImage(): Bitmap? = blur()

    val imageBlur: Bitmap?
        get() = blur()

    companion object {

        const val TAG = "BlurImage"

        private const val MAX_RADIUS = 25f
        private const val MIN_SCALE = 0.2f
        private const val MAX_SCALE = 0.9f

        fun with(context: Context): BlurImage {
            return BlurImage(context)
        }
    }
}