package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.utils.GenericModelFactory

class HorizontalSquarePhotosTypeModel(
    var photosList: List<UnsplashPhotos> = arrayListOf()
): GenericModelFactory() {
    override fun getTotalItems() = photosList.size

    override fun <Type> get(pos: Int): Type {
        return photosList[pos] as Type
    }

    override fun getList(): List<Any> {
        return photosList
    }
}