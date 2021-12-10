package ru.modemastudio.notepro.persistence

import android.content.Context
import dagger.Reusable
import ru.modemastudio.notepro.R
import javax.inject.Inject

@Reusable
class PreferencesManager @Inject constructor(context: Context) {
    private val preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    private val enableAutodeleteKey = context.getString(R.string.enable_autodelete_key)
    private val autodeleteTimeoutKey = context.getString(R.string.autodelete_timeout_key)
    private val darkThemeKey = context.getString(R.string.dark_theme_key)

    val enableAutodelete = preferences.getBoolean(enableAutodeleteKey, false)

    val autodeleteTimeout: Long = when(preferences.getInt(autodeleteTimeoutKey, 6)) {
        1 -> 3_600_000 // Hour
        2 -> 86_400_000 // Day
        3 -> 604_800_000 // Week
        4 -> 2_629_746_000 // Month
        5 -> 15_778_476_000 // Six months
        else -> 31_556_952_000 // Year
    }

    val darkTheme = preferences.getBoolean(darkThemeKey, false)

    companion object {
        private const val preferencesName = "notepro_preferences"
    }
}