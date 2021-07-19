package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.GenericModelFactory

class UserTypeModel(
    var usersList: List<User>
): GenericModelFactory() {
    override fun getTotalItems() = usersList.size

    override fun <Type> get(pos: Int): Type {
        return usersList[pos] as Type
    }

    override fun getList(): List<Any> {
        return usersList
    }
}