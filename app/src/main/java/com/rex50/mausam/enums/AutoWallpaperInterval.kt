package com.rex50.mausam.enums

import java.util.concurrent.TimeUnit

enum class AutoWallpaperInterval(
    val interval: Long,
    val unit: TimeUnit
) {
    ONE_HOUR(1, TimeUnit.HOURS),
    THREE_HOURS(3, TimeUnit.HOURS),
    TWELVE_HOURS(12, TimeUnit.HOURS),
    TWENTY_FOUR_HOURS(24, TimeUnit.HOURS);
}