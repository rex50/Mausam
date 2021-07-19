package com.rex50.mausam.views.activities.collections

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.model_classes.item_types.CollectionTypeModel
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.network.Result
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.toArrayList
import kotlinx.coroutines.launch

class ActCollectionsListViewModel(
    app: Application,
    private val unsplashHelper: UnsplashHelper
): BaseAndroidViewModel(app) {

    var pageTitle = "Photos"
    var pageDesc = ""

    private val mutableLiveCollectionsData: MutableLiveData<CollectionTypeModel> by lazy {
        MutableLiveData<CollectionTypeModel>().also {
            it.value = getEmptyData(pageTitle, pageDesc)
        }
    }

    private val mutableContentLoadingState: MutableLiveData<ContentAnimationState> by lazy {
        MutableLiveData<ContentAnimationState>()
    }

    val collectionsData: LiveData<CollectionTypeModel>
        get() = mutableLiveCollectionsData

    val loadingState: LiveData<ContentAnimationState>
        get() = mutableContentLoadingState

    private var lastPageRequested = INITIAL_PAGE


    var listData = MoreListData()
        set(value) {
            pageTitle = value.getTitle(getApplication())
            pageDesc = value.getDesc()

            val collections = mutableLiveCollectionsData.value?.collections ?: arrayListOf()
            mutableLiveCollectionsData.postValue(getEmptyData(
                pageTitle,
                pageDesc,
                collections
            ))

            field = value
        }

    fun getCollectionsOf(page: Int) {
        lastPageRequested = page
        mutableContentLoadingState.postValue(ContentAnimationState.LOADING)
        when(listData.listMode){
            Constants.ListModes.LIST_MODE_COLLECTIONS -> {
                getFeaturedCollectionsOf(page)
            }

            Constants.ListModes.LIST_MODE_USER_COLLECTIONS -> {
                getUserCollectionsOf(page)
            }
        }
    }

    private fun getFeaturedCollectionsOf(page: Int) = viewModelScope.launch {
        updateList(
            page,
            when(val result = unsplashHelper.getCollectionsAndTags(page, 20)) {
                is Result.Success -> Result.Success(result.data.collectionsList)
                is Result.Failure -> result
            }
        )
    }

    private fun getUserCollectionsOf(page: Int) = viewModelScope.launch {
        updateList(
            page,
            when(val result = unsplashHelper.getUserCollections(listData.photographerInfo?.username ?: "", page, 20)) {
                is Result.Success -> Result.Success(result.data.collectionsList)
                is Result.Failure -> result
            }
        )
    }

    private fun updateList(page: Int, result: Result<List<Collections>>){
        val model = mutableLiveCollectionsData.value
        val list = model?.collections?.toArrayList() ?: arrayListOf()

        when(result) {
            is Result.Success -> {
                if(page == INITIAL_PAGE)
                    list.clear()
                list.addAll(result.data)
                model?.collections = list
                mutableLiveCollectionsData.postValue(model)
                mutableContentLoadingState.postValue(
                    if(list.isEmpty())
                        ContentAnimationState.EMPTY
                    else
                        ContentAnimationState.SUCCESS
                )
            }

            is Result.Failure -> mutableContentLoadingState.postValue(getStateFromFailureResult(result, list.isEmpty()))
        }
    }


    fun retry() {
        getCollectionsOf(lastPageRequested)
    }

    fun isListEmpty(): Boolean {
        return collectionsData.value?.collections?.size ?: 0 == 0
    }


    companion object {

        const val INITIAL_PAGE = 1

        fun getEmptyData(
            title: String = "Photos",
            desc: String = "",
            list: List<Collections> = arrayListOf()
        ) = GenericModelFactory.getCollectionListTypeObject(
            title,
            desc,
            false,
            list
        )
    }
}