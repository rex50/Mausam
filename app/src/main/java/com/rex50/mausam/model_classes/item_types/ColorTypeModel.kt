package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreData
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.Constants
import java.util.*

class ColorTypeModel(
    colorsList: List<ColorModel>,
    shuffle: Boolean
): GenericModelFactory() {
    var colorsList: List<ColorModel> = if(shuffle) colorsList.shuffled() else colorsList
        private set

    override fun getTotalItems() = colorsList.size

    override fun <Type> get(pos: Int): Type {
        return colorsList[pos] as Type
    }

    override fun getList(): List<Any> {
        return colorsList
    }

    fun getAtPos(pos: Int): Any? {
        return colorsList[pos]
    }

    companion object {
        fun createModelFromStringList(colors: List<String>): List<ColorModel> {
            val list: MutableList<ColorModel> = ArrayList()
            for (color in colors) {
                try {
                    val arr = color.split("~~").toTypedArray()
                    list.add(
                        ColorModel(
                            arr[0], arr[1]
                        )
                    )
                } catch (e: NullPointerException) {
                    throw IllegalArgumentException("Invalid color string \"$color\" : Correct format is \"[@colorName]~~#[@colorCode]")
                }
            }
            return list
        }
    }
}

class ColorModel(
    colorName: String,
    colorCode: String
) {
    var colorName: String? = colorName
    var colorCode: String? = colorCode

    fun getMoreListData() = MoreListData(
        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
        generalInfo = MoreData(
            colorName,
            Constants.Providers.POWERED_BY_UNSPLASH
        )
    )

    override fun hashCode(): Int {
        return Objects.hash(colorName)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val color = other as ColorModel
        return Objects.equals(colorName, color.colorName) &&
                Objects.equals(colorCode, color.colorCode)
    }
}