package com.rex50.mausam.enums

import androidx.annotation.DrawableRes
import com.rex50.mausam.R
import java.lang.IllegalArgumentException

enum class MainTabs(val title: String, @DrawableRes val icon: Int) {
    HOME("Home", R.drawable.ic_logo),
    DISCOVER("Discover", R.drawable.ic_search),
    GALLERY("Gallery", R.drawable.ic_heart);

    companion object {
        @JvmStatic
        fun getTab(title: String?): MainTabs = when {
            HOME.title.equals(title, true) -> HOME
            DISCOVER.title.equals(title, true) -> DISCOVER
            GALLERY.title.equals(title, true) -> GALLERY
            else -> {
                throw IllegalArgumentException("No tab found for $title")
            }
        }
    }
}