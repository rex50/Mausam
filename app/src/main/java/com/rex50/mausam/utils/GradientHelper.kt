package com.rex50.mausam.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import com.rex50.mausam.R
import java.util.*

class GradientHelper private constructor() {

    private var lastGradientAccessed = -1
    private var gradientsList: List<GradientDrawable> = arrayListOf()
    private var gradientStrings: Array<String> = arrayOf()

    companion object{
        private var instance: GradientHelper? = null

        fun getInstance(context: Context?): GradientHelper? {
            if (instance == null) {
                synchronized(GradientHelper::class.java) {
                    if (instance == null) {
                        instance = GradientHelper()
                        instance?.gradientsList = createGradientDrawables(context?.resources?.getStringArray(R.array.array_gradients))
                    }
                }
            }else if(instance?.gradientsList.isNullOrEmpty()){
                instance?.gradientsList = createGradientDrawables(context?.resources?.getStringArray(R.array.array_gradients))
            }
            return instance
        }

        fun init(context: Context?){
            getInstance(context)
        }

        private fun createGradientDrawables(gradientStrings: Array<String>?): List<GradientDrawable> {
            val list: MutableList<GradientDrawable> = ArrayList()
            gradientStrings?.apply {
                instance?.gradientStrings = gradientStrings
                for (gradient in this) {
                    val arr = gradient.split("~~")
                    list.add(createTopBottomGradient(Color.parseColor(arr[0]), Color.parseColor(arr[1])))
                }
            }
            return list
        }

        fun createTopBottomGradient(@ColorInt color1: Int, @ColorInt color2: Int): GradientDrawable =
                GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(color1, color2)
                ).also {
                    it.cornerRadius = 16f
                }

        fun createLeftRightGradient(@ColorInt color1: Int, @ColorInt color2: Int): GradientDrawable =
                GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(color1, color2)
                ).also {
                    it.cornerRadius = 16f
                }

        fun createLeftRightGradient(gradient: String, delimiter: String): GradientDrawable {
            val arr = gradient.split(delimiter)
            val color1 = Color.parseColor(arr[0])
            val color2 = Color.parseColor(arr[1])
            return GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(color1, color2)
            ).also {
                it.cornerRadius = 16f
            }
        }
    }

    fun getGradient(): GradientDrawable =
            if(gradientsList.isNullOrEmpty()){
                createTopBottomGradient(Color.parseColor("#000000"), Color.parseColor("#000000"))
            } else if(lastGradientAccessed == -1 || lastGradientAccessed == gradientsList.lastIndex){
                lastGradientAccessed = 0
                gradientsList[lastGradientAccessed]
            } else {
                lastGradientAccessed++
                gradientsList[lastGradientAccessed]
            }

    fun getRandomLeftRightGradient(): GradientDrawable =
            if(gradientStrings.isNullOrEmpty())
                createLeftRightGradient("#58AEF7~~#00B09B", "~~")
            else
                gradientStrings.toList().random().let {
                    createLeftRightGradient(it, "~~")
                }
}