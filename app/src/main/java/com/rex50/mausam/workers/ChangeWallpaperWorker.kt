package com.rex50.mausam.workers

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.net.toUri
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.imageblur.ImageBlur
import com.rex50.mausam.enums.AutoWallpaperInterval
import com.rex50.mausam.enums.DownloadedBy
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.network.Result.*
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.ImageActionHelper
import com.rex50.mausam.utils.getOptimizedBitmap
import org.koin.java.KoinJavaComponent.inject
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

class ChangeWallpaperWorker(
    appContext: Context,
    params: WorkerParameters
): CoroutineWorker(appContext, params) {

    val unsplashHelper by inject(UnsplashHelper::class.java)

    val sharedPrefs by inject(MausamSharedPrefs::class.java)

    override suspend fun doWork(): Result {
        val result = changeWallpaper()

        if(result is Result.Failure) {
            Log.e(TAG, "doWork: ", result.getException())
            FirebaseCrashlytics.getInstance().recordException(result.getException())
        }

        return result
    }

    private suspend fun changeWallpaper(): Result {
        progress("Getting photo details...")
        val downloadedBy = inputData.getString(DownloadedBy.TAG)
        return when(val response = unsplashHelper.getRandomPhoto(topicIdsCommaSeparated = WALLPAPER_TOPICS)) {
            is Success -> {
                if(response.data.isNullOrEmpty()) {
                    Result.failure(errorData("Failed while parsing photo details"))
                } else {
                    downloadPhoto(response.data[0].also {
                        it.downloadedBy = DownloadedBy.getFrom(
                            text = downloadedBy,
                            fallback = DownloadedBy.AUTO_WALLPAPER
                        )
                    })
                }
            }
            is Failure -> Result.failure(errorData("Failed while getting photo details"))
        }
    }

    private suspend fun downloadPhoto(photoData: UnsplashPhotos): Result {
        progress("Downloading photo...")
        return when(val response = ImageActionHelper.saveImage(applicationContext, photoData)) {
            is Success -> {
                modifyAndSetWallpaper(response.data)
            }

            is Failure -> {
                Result.failure(errorData("Failed while downloading photo"))
            }
        }
    }

    private suspend fun modifyAndSetWallpaper(data: UnsplashPhotos): Result {
        try {

            //Apply crop
            progress("Cropping...")
            val bitmap = getCroppedImageOrOptimized(data.relativePath)

            //Apply blur if intensity is greater than 0
            progress("Blurring...")
            val intensity = sharedPrefs.autoWallpaperBlurIntensity
            val blurredBitmap = if(intensity > 0) {
                ImageBlur.with(applicationContext)
                    .load(bitmap)
                    .intensity(intensity)
                    .scale(0.2f)
                    .getBlurredImageAsync()
            } else
                bitmap


            //Set wallpaper
            progress("Setting wallpaper...")
            WallpaperManager.getInstance(applicationContext).setBitmap(blurredBitmap)

            return Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "handleDownload: ", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return Result.failure(errorData("Failed while modifying image"))
    }

    private suspend fun getCroppedImageOrOptimized(path: String): Bitmap? {

        if(sharedPrefs.isEnabledAutoWallpaperCrop) {

            //Decode bitmap
            val bitmap = BitmapFactory.decodeFile(path)

            val displayMetrics = applicationContext.resources.displayMetrics

            return try {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(bitmap)
                    .override(displayMetrics.widthPixels, displayMetrics.heightPixels)
                    .centerCrop()
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                    })
                    .submit()
                    .get()
            } catch (e: Exception) {
                Log.e(TAG, "getCroppedImage: ", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        } else {
            return path.toUri().getOptimizedBitmap(applicationContext)
        }

    }

    private fun errorData(msg: String) = workDataOf(Pair("error", msg))

    private fun Result.getException(): Exception = RuntimeException("$this")

    private suspend fun progress(msg: String) {
        setProgress(workDataOf(Pair(PROGRESS, msg)))
    }


    companion object {

        const val TAG = "ChangeWallpaperWorker"

        private const val WALLPAPER_TOPICS = "bo8jQKTaE0Y,6sMVjTLSkeQ"

        const val CHANGE_WALLPAPER_WORK_NAME = "Mausam_Auto_Change_Wallpaper"

        const val CHANGE_NOW = "ChangeNow"

        const val PROGRESS = "Progress"


        @JvmStatic
        fun getConstraints() = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            //.setRequiresDeviceIdle(false)
            .build()

        @JvmStatic
        fun changeNow(context: Context, downloadedBy: DownloadedBy = DownloadedBy.AUTO_WALLPAPER): UUID {

            val data = Data.Builder()
            data.putString(DownloadedBy.TAG, downloadedBy.text)

            val request = OneTimeWorkRequestBuilder<ChangeWallpaperWorker>()
                .addTag(CHANGE_NOW)
                .setInputData(data.build())
                .build()

            WorkManager.getInstance(context)
                .enqueue(request)

            return request.id
        }

        @JvmStatic
        fun scheduleAutoWallpaper(
            context: Context,
            workPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
        ) {
            val sharedPrefs = MausamSharedPrefs(context)
            if(sharedPrefs.isEnabledAutoWallpaper) {

                val interval: AutoWallpaperInterval = sharedPrefs.autoWallpaperInterval ?: AutoWallpaperInterval.TWENTY_FOUR_HOURS

                val request = PeriodicWorkRequestBuilder<ChangeWallpaperWorker>(
                    interval.interval,
                    interval.unit
                ).setConstraints(getConstraints())
                    .addTag(CHANGE_WALLPAPER_WORK_NAME)
                    .build()

                //Can store requestId for future usage
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        CHANGE_WALLPAPER_WORK_NAME,
                        workPolicy,
                        request
                    )
            }
        }

        @JvmStatic
        fun cancelAutoWallpaper(context: Context) {
            //Can use requestId instead of workName
            WorkManager.getInstance(context)
                .cancelUniqueWork(CHANGE_WALLPAPER_WORK_NAME)
        }

    }

}