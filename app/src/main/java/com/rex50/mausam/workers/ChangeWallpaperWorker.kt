package com.rex50.mausam.workers

import android.app.WallpaperManager
import android.content.Context
import androidx.core.net.toUri
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.imageblur.ImageBlur
import com.rex50.mausam.enums.AutoWallpaperInterval
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.network.Result.*
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.ImageActionHelper
import com.rex50.mausam.utils.getOptimizedBitmap
import com.rex50.mausam.views.activities.auto_wallpaper.ActAutoWallpaper
import org.koin.java.KoinJavaComponent.inject
import java.lang.Exception

class ChangeWallpaperWorker(
    appContext: Context,
    params: WorkerParameters
): CoroutineWorker(appContext, params) {

    val unsplashHelper: UnsplashHelper by inject(UnsplashHelper::class.java)

    override suspend fun doWork(): Result {
        return when(val response = getSinglePhotoData()) {
            is Success -> {
                downloadPhoto(response.data)
            }
            is Failure -> Result.failure()
        }
    }

    private suspend fun getSinglePhotoData(): com.rex50.mausam.network.Result<UnsplashPhotos> {
        return unsplashHelper.getRandomPhoto()
    }

    private suspend fun downloadPhoto(photoData: UnsplashPhotos): Result {
        return when(val response = ImageActionHelper.saveImage(applicationContext, photoData)) {
            is Success -> {
                handleDownload(response.data)
            }

            is Failure -> {
                Result.failure()
            }
        }
    }

    private suspend fun handleDownload(data: UnsplashPhotos): Result {
        try {
            data.relativePath.toUri().let {
                //Compress the photo
                it.getOptimizedBitmap(applicationContext)?.let { bitmap ->

                    //TODO: apply crop

                    //Apply blur
                    val intensity = MausamSharedPrefs(applicationContext).autoWallpaperBlurIntensity
                    val blurredBitmap = ImageBlur.with(applicationContext)
                        .load(bitmap)
                        .intensity(intensity)
                        .scale(0.2f)
                        .getBlurredImageAsync()

                    WallpaperManager.getInstance(applicationContext).setBitmap(blurredBitmap)

                    return Result.success()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return Result.failure()
    }


    companion object {

        const val CHANGE_WALLPAPER_WORK_NAME = "Mausam_Auto_Change_Wallpaper"

        const val TAG = "ChangeWallpaperWorker"


        @JvmStatic
        fun getConstraints() = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            //.setRequiresDeviceIdle(false)
            .build()


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
                ).setConstraints(getConstraints()).build()

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