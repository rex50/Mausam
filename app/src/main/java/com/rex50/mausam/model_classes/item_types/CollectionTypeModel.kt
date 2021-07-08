package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreData
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.Constants

class CollectionTypeModel(
    var collections: List<Collections> = arrayListOf()
): GenericModelFactory() {
    override fun getTotalItems() = collections.size

    override fun <Type> get(pos: Int): Type {
        return collections[pos] as Type
    }

    fun getMoreListData() = MoreListData(
        Constants.ListModes.LIST_MODE_COLLECTIONS,
        generalInfo = MoreData(
            title
        )
    )
}
