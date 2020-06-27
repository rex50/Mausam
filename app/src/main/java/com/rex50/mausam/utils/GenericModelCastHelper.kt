package com.rex50.mausam.utils

import com.rex50.mausam.model_classes.utils.GenericModelFactory

abstract class GenericModelCastHelper(o: Any?) {

    init {
        castObject(o)
    }

    private fun castObject(o: Any?){
        when(o){
            is GenericModelFactory -> findType(o)
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

    private fun findType(o: GenericModelFactory) {
        when{
            o.generalTypeModel.isNotNull() -> castObject(o.generalTypeModel)
            o.userTypeModel.isNotNull() -> castObject(o.userTypeModel)
            o.colorTypeModel.isNotNull() -> castObject(o.colorTypeModel)
            o.collectionTypeModel.isNotNull() -> castObject(o.collectionTypeModel)
            o.tagTypeModel.isNotNull() -> castObject(o.tagTypeModel)
            o.categoryTypeModel.isNotNull() -> castObject(o.categoryTypeModel)
            o.favouritePhotographerTypeModel.isNotNull() -> castObject(o.favouritePhotographerTypeModel)
            else -> this.onError(o)
        }
    }

    open fun onGeneralType(generalTypeModel: GenericModelFactory.GeneralTypeModel?){}
    open fun onCollectionType(collectionTypeModel: GenericModelFactory.CollectionTypeModel?){}
    open fun onUserType(userTypeModel: GenericModelFactory.UserTypeModel?){}
    open fun onColorType(colorTypeModel: GenericModelFactory.ColorTypeModel?){}
    open fun onTagType(tagTypeModel: GenericModelFactory.TagTypeModel?){}
    open fun onCategoryType(categoryTypeModel: GenericModelFactory.CategoryTypeModel?){}
    open fun onFavPhotographerType(favPhotographerTypeModel: GenericModelFactory.FavouritePhotographerTypeModel?){}

    open fun onError(o: Any?){
        throw IllegalArgumentException("Cannot determine given object type.\nHint: Add code for " + o?.javaClass?.name + " class type")
    }

}