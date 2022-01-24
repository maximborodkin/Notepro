package ru.modemastudio.notepro.model

/**
 * Describes possible priorities of a note.
 * Low - without notification
 * Basic - just notification with sound/vibration in status bar (default)
 * High - heads up notification with sound/vibration
 * Alarm - makes a sound until the user turns it off
 * */
enum class ReminderPriority {
    Low,
    Basic,
    High,
    Alarm,
}