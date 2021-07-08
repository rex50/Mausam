package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreData
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.Constants
import java.util.ArrayList

class CategoryTypeModel(
    categories: List<CategoryModel>,
    shuffle: Boolean
): GenericModelFactory() {

    var categories: List<CategoryModel> = if(shuffle) categories.shuffled() else categories
        private set

    override fun getTotalItems() = categories.size

    override fun <Type> get(pos: Int): Type {
        return categories[pos] as Type
    }

    companion object {
        fun createModelFromStringList(categories: List<String>): List<CategoryModel> {
            val list: MutableList<CategoryModel> = ArrayList()
            for (category in categories) {
                try {
                    val arr = category.split("~~").toTypedArray()
                    list.add(CategoryModel(arr[0], arr[1]))
                } catch (e: NullPointerException) {
                    throw IllegalArgumentException("Invalid category string \"$category\" : Correct format is \"[@categoryName]~~[@categoryImage]")
                }
            }
            return list
        }
    }
}

class CategoryModel(
    categoryName: String,
    categoryImg: String
) {
    fun getMoreListData() = MoreListData(
        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
        generalInfo = MoreData(
            categoryName,
            Constants.Providers.POWERED_BY_UNSPLASH
        )
    )

    var categoryName: String? = categoryName
        private set
    var categoryImg: String? = categoryImg
        private set
}