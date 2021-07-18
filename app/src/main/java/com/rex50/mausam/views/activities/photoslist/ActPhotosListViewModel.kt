package com.rex50.mausam.views.activities.photoslist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.model_classes.item_types.HorizontalSquarePhotosTypeModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.network.Result
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS
import com.rex50.mausam.utils.Constants.ListModes.LIST_MODE_GENERAL_PHOTOS
import com.rex50.mausam.utils.Constants.ListModes.LIST_MODE_POPULAR_PHOTOS
import com.rex50.mausam.utils.Constants.ListModes.LIST_MODE_USER_PHOTOS
import com.rex50.mausam.views.fragments.home.FragHomeViewModel
import kotlinx.coroutines.launch

class ActPhotosListViewModel(
    app: Application,
    val unsplashHelper: UnsplashHelper
): BaseAndroidViewModel(app) {

    private var pageTitle = "Photos"
    private var pageDesc = ""

    private val mutableLivePhotosData: MutableLiveData<HorizontalSquarePhotosTypeModel> by lazy {
        MutableLiveData<HorizontalSquarePhotosTypeModel>().also {
            it.value = getEmptyData(pageTitle, pageDesc)
        }
    }

    private val mutableContentLoadingState: MutableLiveData<ContentAnimationState> by lazy {
        MutableLiveData<ContentAnimationState>()
    }

    private var lastPageRequested = INITIAL_PAGE


    val photosData: LiveData<HorizontalSquarePhotosTypeModel>
        get() = mutableLivePhotosData

    val loadingState: LiveData<ContentAnimationState>
        get() = mutableContentLoadingState

    var listData: MoreListData = MoreListData()
        set(value) {
            pageTitle = value.getTitle(getApplication())
            pageDesc = value.getDesc()

            val photos = mutableLivePhotosData.value?.photosList ?: arrayListOf()
            mutableLivePhotosData.postValue(getEmptyData(pageTitle, pageDesc, photos))

            field = value
        }


    fun getPhotosOf(page: Int){
        lastPageRequested = page
        mutableContentLoadingState.postValue(ContentAnimationState.LOADING)
        when(listData.listMode){
            LIST_MODE_POPULAR_PHOTOS -> {
                getPopularPhotosOf(page)
            }

            LIST_MODE_USER_PHOTOS -> {
                getPhotographerPhotosOf(page)
            }

            LIST_MODE_GENERAL_PHOTOS -> {
                getSearchedPhotosOf(page)
            }

            LIST_MODE_COLLECTION_PHOTOS -> {
                getCollectionPhotosOf(page)
            }
        }
    }

    private fun getPopularPhotosOf(page: Int) = viewModelScope.launch {
        updateList(
            page,
            when(val result = unsplashHelper.getPhotosAndUsers(UnsplashHelper.ORDER_BY_POPULAR, page, 20)) {
                is Result.Success -> Result.Success(result.data.photosList)
                is Result.Failure -> result
            }
        )
    }

    private fun getPhotographerPhotosOf(page: Int) = viewModelScope.launch {
        listData.photographerInfo?.username?.let { username ->
            val result = unsplashHelper.getUserPhotos(username, page, 20)
            updateList(page, result)
        } ?: updateList(page, Result.Failure(IllegalArgumentException("Invalid username")))
    }

    private fun getCollectionPhotosOf(page: Int) = viewModelScope.launch {
        listData.collectionInfo?.id?.let { collectionId ->
            updateList(
                page,
                when(val result = unsplashHelper.getCollectionPhotos(collectionId, page, 20)) {
                    is Result.Success -> Result.Success(result.data.photosList)
                    is Result.Failure -> result
                }
            )
        } ?: updateList(page, Result.Failure(IllegalArgumentException("Invalid collectionId")))
    }

    private fun getSearchedPhotosOf(page: Int) = viewModelScope.launch {
        listData.generalInfo?.term?.let { searchTerm ->
            updateList(
                page,
                when(val result = unsplashHelper.getSearchedPhotos(searchTerm, page, 20)) {
                    is Result.Success -> Result.Success(result.data.results)
                    is Result.Failure -> result
                }
            )
        } ?: updateList(page, Result.Failure(IllegalArgumentException("Invalid search term")))
    }

    private fun updateList(page: Int, result: Result<List<UnsplashPhotos>>) {
        val model = mutableLivePhotosData.value
        val list = model?.photosList?.toArrayList() ?: arrayListOf()

        when (result) {
            is Result.Success -> {
                if(page == FragHomeViewModel.INITIAL_PAGE) {
                    list.clear()
                }
                list.addAll(result.data)
                model?.photosList = list
                mutableLivePhotosData.postValue(model)
                mutableContentLoadingState.postValue(
                    if(list.isEmpty())
                        ContentAnimationState.EMPTY
                    else
                        ContentAnimationState.SUCCESS
                )
            }

            is Result.Failure -> {
                mutableContentLoadingState.postValue(getStateFromFailureResult(result, list.isEmpty()))
            }
        }
    }

    fun getErrorMessage() = when (listData.listMode) {
        LIST_MODE_USER_PHOTOS -> {
            if(isListEmpty()) "No public photos found of ${listData.photographerInfo?.name}" else null
        }

        else -> null
    }

    fun isListEmpty() = photosData.value?.photosList?.size ?: 0 == 0

    fun retry() {
        getPhotosOf(lastPageRequested)
    }

    val isShowPhotographer: Boolean
        get() = listData.photographerInfo?.isNull() ?: true

    fun getLiveDownloadStatus(): LiveData<ImageActionHelper.DownloadStatus> = imageDownloadStatus


    fun getPageTitle(): String {
        return listData.getTitle(getApplication())
    }

    fun getPageDesc(): String {
        return listData.getDesc()
    }

    fun getPhotographerInfo(): User? {
        return listData.photographerInfo
    }

    companion object {

        const val INITIAL_PAGE: Int = 1

        fun getEmptyData(
            title: String = "Photos",
            desc: String = "",
            list: List<UnsplashPhotos> = arrayListOf()
        ) = GenericModelFactory.getHorizontalSquarePhotosTypeObject(
            title,
            desc,
            list
        )
    }

}