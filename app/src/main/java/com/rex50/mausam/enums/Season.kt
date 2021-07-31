package com.rex50.mausam.enums

import com.rex50.mausam.utils.addAfter
import org.joda.time.LocalDateTime
import java.util.Calendar.*

enum class Season(val text: String) {
    SPRING("Spring"),
    SUMMER("Summer"),
    MONSOON("Monsoon"),
    AUTUMN("Autumn"),
    PRE_WINTER("Early Winter"),
    WINTER("Winter");

    companion object {
        @JvmStatic
        fun current(): Season {
            return of(LocalDateTime.now().monthOfYear)
        }

        @JvmStatic
        fun searchTermFor(season: Season): String = when (season) {
            SPRING -> SPRING.text + " wind"
            SUMMER -> SUMMER.text + " hot"
            MONSOON -> MONSOON.text + " rainy"
            AUTUMN -> AUTUMN.text + " fall"
            PRE_WINTER -> PRE_WINTER.text + " cold"
            WINTER -> WINTER.text + " snow"
        }.addAfter(" photos and wallpapers")

        @JvmStatic
        fun of(month: Int): Season {
            return when (month) {
                MARCH -> SPRING
                APRIL -> SPRING
                MAY -> SUMMER
                JUNE -> SUMMER
                JULY -> MONSOON
                AUGUST -> MONSOON
                SEPTEMBER -> AUTUMN
                OCTOBER -> AUTUMN
                NOVEMBER -> PRE_WINTER
                DECEMBER -> PRE_WINTER
                JANUARY -> WINTER
                FEBRUARY -> WINTER
                else -> {
                    SUMMER
                }
            }
        }
    }

}