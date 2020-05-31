package com.rex50.mausam.utils

import com.rex50.mausam.model_classes.utils.GenericModelFactory

abstract class GenericModelCastHelper(o: Any?) {

    init {
        when(o){
            is GenericModelFactory.GeneralTypeModel -> this.onGeneralType(o)
            is GenericModelFactory.UserTypeModel -> this.onUserType(o)
            is GenericModelFactory.ColorTypeModel -> this.onColorType(o)
            is GenericModelFactory.CollectionTypeModel -> this.onCollectionType(o)
            is GenericModelFactory.TagTypeModel -> this.onTagType(o)
            is GenericModelFactory.CategoryTypeModel -> this.onCategoryType(o)
            is GenericModelFactory.FavouritePhotographerTypeModel -> this.onFavPhotographerType(o)
            else -> this.onError(o)
        }
    }

    abstract fun onGeneralType(generalTypeModel: GenericModelFactory.GeneralTypeModel?)
    abstract fun onCollectionType(collectionTypeModel: GenericModelFactory.CollectionTypeModel?)

    open fun onUserType(userTypeModel: GenericModelFactory.UserTypeModel?){}
    open fun onColorType(colorTypeModel: GenericModelFactory.ColorTypeModel?){}
    open fun onTagType(tagTypeModel: GenericModelFactory.TagTypeModel?){}
    open fun onCategoryType(categoryTypeModel: GenericModelFactory.CategoryTypeModel?){}
    open fun onFavPhotographerType(favPhotographerTypeModel: GenericModelFactory.FavouritePhotographerTypeModel?){}

    open fun onError(o: Any?){
        throw IllegalArgumentException("Cannot determine given object type.\nHint: Add code for " + o?.javaClass?.name + " class type")
    }

}