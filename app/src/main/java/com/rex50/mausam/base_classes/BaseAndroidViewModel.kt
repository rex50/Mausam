package com.rex50.mausam.base_classes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.network.Result
import com.rex50.mausam.utils.ConnectionChecker
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.ImageActionHelper
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.Func
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseAndroidViewModel(app: Application): AndroidViewModel(app), KoinComponent {

    protected val connectionChecker by inject<ConnectionChecker>()

    protected val imageDownloadStatus: MutableLiveData<ImageActionHelper.DownloadStatus> by lazy {
        MutableLiveData<ImageActionHelper.DownloadStatus>()
    }

    private var downloadingReq: Request? = null

    fun favouriteImage(photoInfo: UnsplashPhotos, onSuccess: ((UnsplashPhotos?, String) -> Unit)? = null) {
        saveImage(photoInfo, true, onSuccess)
    }

    fun downloadImage(photoInfo: UnsplashPhotos, onDownloadSuccess: ((UnsplashPhotos?, String) -> Unit)? = null) {
        if(connectionChecker.isNetworkConnected()) {
            saveImage(photoInfo, false, onDownloadSuccess)
        } else {
            imageDownloadStatus.postValue(ImageActionHelper.DownloadStatus.Error(Constants.Network.NO_INTERNET))
        }
    }

    fun cancelDownloadImage(onSuccess: Func<Download>? = null, onError: Func<Error>? = null) {
        downloadingReq?.id?.let { id ->
            val fetchConfiguration = FetchConfiguration.Builder(getApplication())
                .setDownloadConcurrentLimit(3)
                .build()

            val fetch = Fetch.getInstance(fetchConfiguration)
            fetch.cancel(id, {
                onSuccess?.call(it)
            }, {
                onError?.call(it)
            })
        } ?: onError?.call(Error.FAILED_TO_UPDATE_REQUEST)
    }

    protected fun saveImage(
        photoInfo: UnsplashPhotos,
        forFav: Boolean,
        onSuccess: ((UnsplashPhotos?, String) -> Unit)?
    ) {
        ImageActionHelper.saveImage(getApplication(), photoInfo, forFav, object : ImageActionHelper.ImageSaveListener{
            override fun onDownloadStarted(request: Request) {
                downloadingReq = request
                imageDownloadStatus.postValue (
                    ImageActionHelper.DownloadStatus.Started(0)
                )
            }

            override fun onDownloadFailed(msg: String) {
                imageDownloadStatus.postValue(
                    ImageActionHelper.DownloadStatus.Error(msg)
                )
            }

            override fun onDownloadProgress(progress: String) {
                imageDownloadStatus.postValue(
                    ImageActionHelper.DownloadStatus.Downloading(progress)
                )
            }

            override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                imageDownloadStatus.postValue(
                    ImageActionHelper.DownloadStatus.Success(imageMeta)
                )
                onSuccess?.invoke(imageMeta, msg)
            }
        })
    }

    protected fun getStateFromFailureResult(result: Result.Failure, isListEmpty: Boolean): ContentAnimationState {
        return when {
            result.exception.message.equals(Constants.Network.NO_INTERNET) || !connectionChecker.isNetworkConnected() -> {
                ContentAnimationState.NO_INTERNET
            }

            isListEmpty -> {
                ContentAnimationState.EMPTY
            }

            else -> {
                ContentAnimationState.ERROR
            }
        }
    }

}