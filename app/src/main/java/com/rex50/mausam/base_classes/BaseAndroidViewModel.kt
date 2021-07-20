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
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseAndroidViewModel(app: Application): AndroidViewModel(app), KoinComponent {

    protected val connectionChecker by inject<ConnectionChecker>()

    protected val imageDownloadStatus: MutableLiveData<ImageActionHelper.DownloadStatus> by lazy {
        MutableLiveData<ImageActionHelper.DownloadStatus>()
    }

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

    protected fun saveImage(
        photoInfo: UnsplashPhotos,
        forFav: Boolean,
        onSuccess: ((UnsplashPhotos?, String) -> Unit)?
    ) {
        ImageActionHelper.saveImage(getApplication(), photoInfo, forFav, object : ImageActionHelper.ImageSaveListener{
            override fun onDownloadStarted() {
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
            result.exception.message.equals(Constants.Network.NO_INTERNET)-> {
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