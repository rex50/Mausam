package com.rex50.mausam.enums

import android.util.Log

enum class DownloadQuality(val text: String) {
    SMALL("Small"),
    REGULAR("Regular"),
    FULL("Full"),
    RAW("Raw");

    companion object {
        @JvmStatic
        fun getQuality(quality: String): DownloadQuality {
            return when(quality) {
                SMALL.text -> SMALL
                REGULAR.text -> REGULAR
                FULL.text -> FULL
                RAW.text -> RAW
                else -> {
                    Log.e("DownloadQuality", "getQuality: Invalid quality type")
                    FULL
                }
            }
        }
    }
}