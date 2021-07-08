package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.utils.GenericModelFactory

class TagTypeModel(
    tagsList: List<Tag>,
    shuffle: Boolean
): GenericModelFactory() {
    var tagsList: List<Tag> = if(shuffle) tagsList.shuffled() else tagsList
        private set

    override fun getTotalItems() = tagsList.size

    override fun <Type> get(pos: Int): Type {
        return tagsList[pos] as Type
    }
}