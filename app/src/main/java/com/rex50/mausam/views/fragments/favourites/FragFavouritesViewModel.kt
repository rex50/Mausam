package com.rex50.mausam.views.fragments.favourites

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragFavouritesViewModel(application: Application) : AndroidViewModel(application) {

    //List of currently downloaded photos in user's storage
    private var downloadedPhotosList: LiveData<ArrayList<UnsplashPhotos>>? = null

    //List of interested photographers which is
    // prepared from downloaded photos list.
    private val interestedPhotographers: MutableLiveData<ArrayList<User>>? by lazy {
        MutableLiveData<ArrayList<User>>()
    }

    //This observer observes downloaded photos list
    // and prepare a list of photographers from downloaded photos
    private val photographerObserver: Observer<ArrayList<UnsplashPhotos>> by lazy {
        Observer { photos ->
            viewModelScope.launch {
                updatePhotographers(photos)
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
        downloadedPhotosList?.observeForever(photographerObserver)
    }

    private suspend fun initDownloadedPhotoList() {
        downloadedPhotosList = KeyValuesRepository.getDownloadedPhotos(getApplication())
        Log.e("FragFav", "Downloaded photos: ${downloadedPhotosList?.value?.size}")
    }

    private suspend fun updatePhotographers(photos: ArrayList<UnsplashPhotos>) = withContext(Dispatchers.IO) {
        val photographers = arrayListOf<User>()
        photos.forEach { photo ->
            photographers.add(photo.user)
        }
        withContext(Dispatchers.Main) {
            interestedPhotographers?.value = photographers
        }
    }

    override fun onCleared() {
        downloadedPhotosList?.removeObserver(photographerObserver)
        super.onCleared()
    }

    fun getDownloadedPhotos(): LiveData<ArrayList<UnsplashPhotos>>? {
        return downloadedPhotosList
    }

    fun getInterestedPhotographers(): LiveData<ArrayList<User>>? {
        return interestedPhotographers
    }


}