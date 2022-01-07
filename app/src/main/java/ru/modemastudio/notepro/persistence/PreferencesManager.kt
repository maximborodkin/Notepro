package ru.modemastudio.notepro.persistence

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Reusable
import ru.modemastudio.notepro.R
import javax.inject.Inject

@Reusable
class PreferencesManager @Inject constructor(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val enableAutodeleteKey = context.getString(R.string.enable_autodelete_key)
    private val autodeleteTimeoutKey = context.getString(R.string.autodelete_timeout_key)
    private val darkThemeKey = context.getString(R.string.dark_theme_key)
    private val onboardingDoneKey = context.getString(R.string.onboarding_done_key)
    val enabledFeaturesKey = context.getString(R.string.enabled_features_key)

    val isAutodeleteEnabled = preferences.getBoolean(enableAutodeleteKey, false)

    // Returns milliseconds of a selected timeout
    val autodeleteTimeout: Long = with(context) {
        when (preferences.getString(autodeleteTimeoutKey, null)
        ) {
            getString(R.string.autodelete_timeout_one_hour) -> 3_600_000
            getString(R.string.autodelete_timeout_one_day) -> 86_400_000
            getString(R.string.autodelete_timeout_one_week) -> 604_800_000
            getString(R.string.autodelete_timeout_one_month) -> 2_629_746_000
            getString(R.string.autodelete_timeout_six_months) -> 15_778_476_000
            else -> 31_556_952_000 // Year
        }
    }

    val isDarkTheme = preferences.getBoolean(darkThemeKey, false)

    var isOnboardingDone: Boolean
        get() = preferences.getBoolean(onboardingDoneKey, false)
        set(value) = preferences.edit().putBoolean(onboardingDoneKey, value).apply()
}