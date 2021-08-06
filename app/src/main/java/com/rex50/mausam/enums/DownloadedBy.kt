package com.rex50.mausam.enums

enum class DownloadedBy(val text: String) {
    USER("User"), AUTO_WALLPAPER("Auto Wallpaper"), FRESH_WALLPAPER("Fresh Wallpaper");

    companion object {

        const val TAG = "DownloadedBy"

        @JvmStatic
        fun getFrom(text: String?) = getFrom(text, USER)

        @JvmStatic
        fun getFrom(text: String?, fallback: DownloadedBy = USER) = when(text) {
            USER.text -> USER
            AUTO_WALLPAPER.text -> AUTO_WALLPAPER
            FRESH_WALLPAPER.text -> FRESH_WALLPAPER
            else -> fallback
        }
    }
}