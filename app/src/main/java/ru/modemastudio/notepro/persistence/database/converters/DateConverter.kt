package ru.modemastudio.notepro.persistence.database.converters

import androidx.room.TypeConverter
import java.util.*

object DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}