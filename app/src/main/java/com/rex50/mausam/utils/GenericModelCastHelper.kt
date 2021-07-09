package com.rex50.mausam.utils

import com.rex50.mausam.model_classes.item_types.*
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import java.lang.Exception

abstract class GenericModelCastHelper(o: Any?) {

    init {
        castObject(o)
    }

    private fun castObject(o: Any?){
        when(o){
            is GeneralTypeModel -> this.onGeneralType(o)
            is UserTypeModel -> this.onUserType(o)
            is ColorTypeModel -> this.onColorType(o)
            is CollectionTypeModel -> this.onCollectionType(o)
            is TagTypeModel -> this.onTagType(o)
            is CategoryTypeModel -> this.onCategoryType(o)
            is HorizontalSquarePhotosTypeModel -> this.onHorizontalSquarePhotosTypeModel(o)
            else -> this.onError(o)
        }
    }

    open fun onGeneralType(generalTypeModel: GeneralTypeModel){}
    open fun onCollectionType(collectionTypeModel: CollectionTypeModel){}
    open fun onUserType(userTypeModel: UserTypeModel){}
    open fun onColorType(colorTypeModel: ColorTypeModel){}
    open fun onTagType(tagTypeModel: TagTypeModel){}
    open fun onCategoryType(categoryTypeModel: CategoryTypeModel){}
    open fun onHorizontalSquarePhotosTypeModel(horizontalSquarePhotosTypeModel: HorizontalSquarePhotosTypeModel){}

    open fun onError(o: Any?){
        throw IllegalArgumentException("Cannot determine given object type.\nHint: Add code for " + o?.javaClass?.name + " class type")
    }

    companion object {

        inline fun <reified Type> cast(model: GenericModelFactory?): Type? {
            return try {
                if(model is Type) model else null
            } catch (e: Exception) {
                null
            }
        }

    }

}