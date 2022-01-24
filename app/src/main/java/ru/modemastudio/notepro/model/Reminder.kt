package ru.modemastudio.notepro.model

import java.util.Date

data class Reminder (
    val noteId: Long,
    val date: Date,
    val priority: ReminderPriority
)