package com.rex50.mausam.views.fragments.home

import android.app.Application
import androidx.lifecycle.*
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.model_classes.item_types.HorizontalSquarePhotosTypeModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.network.Result
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.AnimatedMessage.AnimationByState
import com.rex50.mausam.utils.Constants.Util.userFavConstants
import kotlinx.coroutines.launch

class FragHomeViewModel(
    application: Application,
    private val unsplashHelper: UnsplashHelper,
    val connectionChecker: ConnectionChecker
) : BaseAndroidViewModel(application) {

    private val mutableLiveHomeContent: MutableLiveData<HorizontalSquarePhotosTypeModel> by lazy {
        MutableLiveData<HorizontalSquarePhotosTypeModel>().also {
            it.value = getEmptyData()
        }
    }

    private val mutableContentLoadingState: MutableLiveData<ContentAnimationState> by lazy {
        MutableLiveData<ContentAnimationState>()
    }

    private var lastPageRequested = INITIAL_PAGE

    val animations: ArrayList<AnimationByState<ContentAnimationState>> by lazy {
        val context = getApplication<Application>().applicationContext
        arrayListOf(
            AnimationByState(
                ContentAnimationState.NO_INTERNET,
                R.raw.l_anim_error_no_internet,
                context.getString(R.string.error_no_internet),
                context.getString(R.string.retry)
            ),
            AnimationByState(
                ContentAnimationState.EMPTY,
                R.raw.l_anim_error_astronaout,
                context.getString(R.string.msg_empty_discover, userFavConstants.random()),
                context.getString(R.string.action_start_search)
            )
        )
    }

    init {
        getLatestPhotosOf(INITIAL_PAGE)
    }

    val homeContent: LiveData<HorizontalSquarePhotosTypeModel> = mutableLiveHomeContent

    val loadingState: LiveData<ContentAnimationState> = mutableContentLoadingState

    fun getLiveDownloadStatus(): LiveData<ImageActionHelper.DownloadStatus> = imageDownloadStatus

    fun getLatestPhotosOf(page: Int) = viewModelScope.launch {
        lastPageRequested = page
        mutableContentLoadingState.postValue(ContentAnimationState.LOADING)
        updateList(
            page,
            when(val result = unsplashHelper.getPhotosAndUsers(UnsplashHelper.ORDER_BY_LATEST, page, 20)) {
                is Result.Success -> Result.Success(result.data.photosList)
                is Result.Failure -> result
            }
        )
    }

    private fun updateList(page: Int, result: Result<List<UnsplashPhotos>>) {
        val model = mutableLiveHomeContent.value
        val list = model?.photosList?.toArrayList() ?: arrayListOf()

        when (result) {
            is Result.Success -> {
                if(page == INITIAL_PAGE) {
                    list.clear()
                }
                list.addAll(result.data)
                model?.photosList = list
                mutableLiveHomeContent.postValue(model)
                mutableContentLoadingState.postValue(ContentAnimationState.SUCCESS)
            }

            is Result.Failure -> {
                mutableContentLoadingState.postValue(getStateFromFailureResult(result, list.isEmpty()))
            }
        }
    }

    fun reload() {
        getLatestPhotosOf(lastPageRequested)
    }

    companion object {
        const val INITIAL_PAGE: Int = 1

        fun getEmptyData() = GenericModelFactory.getHorizontalSquarePhotosTypeObject(
            Constants.AvailableLayouts.POPULAR_PHOTOS,
            Constants.Providers.POWERED_BY_UNSPLASH,
            arrayListOf()
        )
    }

}