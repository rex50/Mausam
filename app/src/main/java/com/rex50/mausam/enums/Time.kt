package com.rex50.mausam.enums

import com.rex50.mausam.utils.addAfter
import java.util.*

enum class Time(val text: String) {
    MID_NIGHT("Mid Night"),
    NIGHT("Night"),
    LATE_NIGHT("Late Night"),
    MORNING("Morning"),
    NOON("Noon"),
    AFTERNOON("Afternoon"),
    EVENING("Evening");

    companion object {

        fun searchTermFor(time: Time): String = when(time) {
            MID_NIGHT -> MID_NIGHT.text
            NIGHT -> NIGHT.text
            LATE_NIGHT -> LATE_NIGHT.text
            MORNING -> MORNING.text
            NOON -> NOON.text
            AFTERNOON -> AFTERNOON.text
            EVENING -> EVENING.text
        }.addAfter(" photos and wallpapers")

        @JvmStatic
        fun current(): Time {
            val c: Calendar = Calendar.getInstance()
            return of(c.get(Calendar.HOUR_OF_DAY))
        }

        @JvmStatic
        fun of(timeOfDay: Int): Time {
            return when {
                timeOfDay < 5 -> MID_NIGHT
                timeOfDay < 6 -> LATE_NIGHT
                timeOfDay < 12 -> MORNING
                timeOfDay < 14 -> NOON
                timeOfDay < 16 -> AFTERNOON
                timeOfDay < 21 -> EVENING
                else -> NIGHT
            }
        }

    }
}