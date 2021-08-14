package com.rex50.mausam.enums

import androidx.annotation.DrawableRes
import com.rex50.mausam.R
import java.lang.IllegalArgumentException

enum class MainTabs(val title: String, @DrawableRes val icon: Int) {
    HOME("Latest", R.drawable.ic_latest),
    DISCOVER("Discover", R.drawable.ic_search),
    GALLERY("Home", R.drawable.ic_home);

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