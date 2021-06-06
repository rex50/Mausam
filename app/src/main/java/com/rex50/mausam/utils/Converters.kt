package com.rex50.mausam.utils

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {

    @TypeConverter
    fun toString(dateTime: DateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toDateTime(string: String): DateTime {
        return DateTime.parse(string)
    }

}