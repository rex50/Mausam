package com.rex50.mausam.enums

import java.util.concurrent.TimeUnit

enum class AutoWallpaperInterval(
    val interval: Long,
    val unit: TimeUnit
) {
    //UI will be generated automatically for below enums
    // So adding a new enum over here will be visible in UI
    FIFTEEN_MINUTES(15, TimeUnit.MINUTES),
    THIRTY_MINUTES(30, TimeUnit.MINUTES),
    //FORTY_FIVE_MINUTES(45, TimeUnit.MINUTES),
    ONE_HOUR(1, TimeUnit.HOURS),
    //TWO_HOURS(2, TimeUnit.HOURS),
    THREE_HOURS(3, TimeUnit.HOURS),
    //FOUR_HOURS(4, TimeUnit.HOURS),
    //FIVE_HOURS(5, TimeUnit.HOURS),
    SIX_HOURS(6, TimeUnit.HOURS),
    TWELVE_HOURS(12, TimeUnit.HOURS),
    //SIXTEEN_HOURS(16, TimeUnit.HOURS),
    TWENTY_FOUR_HOURS(24, TimeUnit.HOURS);
}