package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.utils.GenericModelFactory

class CollectionTypeModel(
    var collections: List<Collections> = arrayListOf()
): GenericModelFactory() {
    override fun getTotalItems() = collections.size

    override fun <Type> get(pos: Int): Type {
        return collections[pos] as Type
    }
}
