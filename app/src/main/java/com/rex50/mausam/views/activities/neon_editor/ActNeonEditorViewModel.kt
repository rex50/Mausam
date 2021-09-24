package com.rex50.mausam.views.activities.neon_editor

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.network.Result
import com.rex50.mausam.network.Status
import com.rex50.mausam.network.UnsplashHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActNeonEditorViewModel(
    app: Application,
    private val unsplashHelper: UnsplashHelper
): BaseAndroidViewModel(app) {

    private val mutablePhotoData: MutableStateFlow<Status<UnsplashPhotos>> = MutableStateFlow(Status.Loading)
    val photoData: StateFlow<Status<UnsplashPhotos>> = mutablePhotoData

    fun getRandomPhoto() = viewModelScope.launch {
        mutablePhotoData.emit(Status.Loading)
        when(val photoResult = unsplashHelper.getRandomPhoto(searchTerm = "Person", responseExpiryInHours = 1)) {
            is Result.Success -> {
                mutablePhotoData.emit(Status.Success(photoResult.data[0]))
            }

            is Result.Failure -> {
                mutablePhotoData.emit(Status.Error("Error while getting photo details", photoResult.exception))
            }
        }
    }

    /*private fun startPhotoDownload(unsplashPhotos: UnsplashPhotos) {
        TODO("Not yet implemented")
    }*/

    override fun onCleared() {
        super.onCleared()
    }

}