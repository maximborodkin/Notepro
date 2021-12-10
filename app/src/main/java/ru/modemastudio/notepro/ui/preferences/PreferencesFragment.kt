package ru.modemastudio.notepro.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.modemastudio.notepro.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}