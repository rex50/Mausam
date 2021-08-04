package com.rex50.mausam.enums

enum class DownloadedBy(val text: String) {
    USER("User"), AUTO_WALLPAPER("Auto Wallpaper");

    companion object {
        @JvmStatic
        fun getFrom(text: String) = when(text) {
            USER.text -> USER
            AUTO_WALLPAPER.text -> AUTO_WALLPAPER
            else -> USER
        }
    }
}