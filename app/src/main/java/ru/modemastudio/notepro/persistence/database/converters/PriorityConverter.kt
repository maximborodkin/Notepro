package ru.modemastudio.notepro.persistence.database.converters

import androidx.room.TypeConverter
import ru.modemastudio.notepro.model.ReminderPriority

object PriorityConverter {

    @TypeConverter
    fun fromPriority(priority: ReminderPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): ReminderPriority {
        return ReminderPriority.valueOf(priority)
    }

}