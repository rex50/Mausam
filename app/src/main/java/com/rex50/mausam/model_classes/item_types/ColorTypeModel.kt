package com.rex50.mausam.model_classes.item_types

import com.rex50.mausam.model_classes.utils.GenericModelFactory
import java.util.ArrayList

class ColorTypeModel(
    colorsList: List<ColorModel?>,
    shuffle: Boolean
): GenericModelFactory() {
    var colorsList: List<ColorModel?> = if(shuffle) colorsList.shuffled() else colorsList
        private set

    override fun getTotalItems() = colorsList.size

    override fun <Type> get(pos: Int): Type {
        return colorsList[pos] as Type
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
}