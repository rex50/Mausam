package com.rex50.mausam.views.fragments.favourites

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import com.rex50.mausam.utils.Constants.AvailableLayouts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragFavouritesViewModel(application: Application, var repository: KeyValuesRepository) : AndroidViewModel(application) {

    private val allSections: AllContentModel? by lazy {
        AllContentModel().also {
            it.setSequenceOfLayouts(mutableListOf<String>().also { list ->
                list.add(AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS)
                list.add(AvailableLayouts.DOWNLOADED_PHOTOS)
                //list.add(AvailableLayouts.FAVOURITE_PHOTOS)
            })
        }
    }

    //List of currently downloaded photos in user's storage
    private var downloadedPhotosList: LiveData<ArrayList<UnsplashPhotos>>? = null


    //This observer observes downloaded photos list
    // and prepare a list of photographers from downloaded photos
    private val downloadedPhotosObserver: Observer<ArrayList<UnsplashPhotos>> by lazy {
        Observer { photos ->
            viewModelScope.launch {

                if(photos.isNotEmpty()) {

                    photos.reverse()

                    addOrUpdateDownloadedList(photos)

                    addOrUpdatePhotographersList(photos)

                    //TODO:
                    //  addOrUpdateFavPhotoList()

                } else {
                    allSections?.clearList()
                }

            }
        }
    }

    init {
        viewModelScope.launch {
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
        allSections?.addOrUpdateModel(
            AvailableLayouts.DOWNLOADED_PHOTOS,
            GenericModelFactory.getDownloadedSectionTypeObject(
                AvailableLayouts.DOWNLOADED_PHOTOS,
                R.drawable.ic_downloaded,
                false,
                photos
            )
        )
    }

    private suspend fun addOrUpdatePhotographersList(photos: ArrayList<UnsplashPhotos>) = withContext(Dispatchers.IO) {
        if(photos.size >= 2) {
            val photographers = arrayListOf<User>()
            photos.forEach { photo ->
                photographers.add(photo.user)
            }

            allSections?.addOrUpdateModel(
                AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS,
                GenericModelFactory.getRecommendedUserSectionTypeObject(
                    AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS,
                    R.drawable.ic_star,
                    false,
                    photographers
                )
            )
        } else {
            allSections?.remove(AvailableLayouts.RECOMMENDED_PHOTOGRAPHERS)
        }
    }

    private suspend fun addOrUpdateFavPhotoList() {
        allSections?.addOrUpdateModel(
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

    fun getSectionsLiveData(): LiveData<AllContentModel>? {
        return allSections?.getModelLiveList()
    }

}