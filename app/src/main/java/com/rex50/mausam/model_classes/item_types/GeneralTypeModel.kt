package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreData
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.Constants

class GeneralTypeModel(
    var photosList: List<UnsplashPhotos> = arrayListOf()
): GenericModelFactory() {
    override fun getTotalItems() = photosList.size

    override fun <Type> get(pos: Int): Type {
        return photosList[pos] as Type
    }

    override fun getList(): List<Any> {
        return photosList
    }

    fun getMoreListData() = MoreListData(
        Constants.ListModes.LIST_MODE_POPULAR_PHOTOS,
        generalInfo = MoreData(
            title,
            Constants.Providers.POWERED_BY_UNSPLASH
        )
    )
}