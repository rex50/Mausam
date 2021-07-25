package com.rex50.mausam.views.fragments.favourites

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.enums.ContentLoadingState
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import com.rex50.mausam.utils.Constants.AvailableLayouts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragGalleryViewModel(application: Application, var repository: KeyValuesRepository) : BaseAndroidViewModel(application) {

    private val allSections: AllContentModel by lazy {
        AllContentModel()
    }

    //List of currently downloaded photos in user's storage
    private var downloadedPhotosList: LiveData<ArrayList<UnsplashPhotos>>? = null


    //This observer observes downloaded photos list
    // and prepare a list of photographers from downloaded photos
    private val downloadedPhotosObserver: Observer<ArrayList<UnsplashPhotos>> by lazy {
        Observer { photos ->

                if(photos.isNotEmpty()) {

                    viewModelScope.launch {

                        photos.reverse()

                        addOrUpdateDownloadedList(photos)

                        addOrUpdatePhotographersList(photos)

                        //TODO:
                        //  addOrUpdateFavPhotoList()

                    }

                } else {
                    allSections.clearList()
                }

        }
    }

    val loadingState: LiveData<ContentLoadingState<AllContentModel>> = allSections.contentLoadingState

    init {
        viewModelScope.launch {
            delay(300)
            initDownloadedPhotoList()
            setupObservers()
        }
    }

    private fun setupObservers() {
        downloadedPhotosList?.observeForever(downloadedPhotosObserver)
    }

    private suspend fun initDownloadedPhotoList() {
        downloadedPhotosList = repository.getDownloadedPhotos()
        //Log.e("FragFav", "Downloaded photos: ${downloadedPhotosList?.value?.size}")
    }

    private suspend fun addOrUpdateDownloadedList(photos: ArrayList<UnsplashPhotos>) = withContext(Dispatchers.IO) {
        if(photos.size == 0) {
            allSections.removeSection(AvailableLayouts.DOWNLOADED_PHOTOS)
            allSections.remove(AvailableLayouts.DOWNLOADED_PHOTOS)
        } else {
            allSections.addSection(0, AvailableLayouts.DOWNLOADED_PHOTOS)
            allSections.addOrUpdateModel(
                AvailableLayouts.DOWNLOADED_PHOTOS,
                GenericModelFactory.getDownloadedSectionTypeObject(
                    AvailableLayouts.DOWNLOADED_PHOTOS,
                    R.drawable.ic_downloaded,
                    false,
                    photos
                )
            )
        }
    }

    private suspend fun addOrUpdatePhotographersList(photos: ArrayList<UnsplashPhotos>) = withContext(Dispatchers.IO) {
        val photographers = photos.map { it.user }.distinctBy { it.id }
        if(photographers.size >= 2) {
            //Update sequence
            allSections.addSection(0, AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS)

            allSections.addOrUpdateModel(
                AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS,
                GenericModelFactory.getRecommendedUserSectionTypeObject(
                    AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS,
                    R.drawable.ic_star,
                    false,
                    photographers
                )
            )
        } else {
            //Update sequence
            allSections.removeSection(AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS)

            allSections.remove(AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS)
        }
    }

    private suspend fun addOrUpdateFavPhotoList() {
        allSections.addOrUpdateModel(
            AvailableLayouts.FAVOURITE_PHOTOS,
            GenericModelFactory.getDownloadedSectionTypeObject(
                AvailableLayouts.FAVOURITE_PHOTOS,
                R.drawable.ic_heart,
                false,
                arrayListOf()
            )
        )
    }

    override fun onCleared() {
        downloadedPhotosList?.removeObserver(downloadedPhotosObserver)
        super.onCleared()
    }

    fun getSectionsLiveData(): LiveData<AllContentModel> {
        return allSections.getModelLiveList()
    }

}