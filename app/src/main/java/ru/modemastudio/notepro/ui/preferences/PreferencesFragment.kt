package ru.modemastudio.notepro.ui.preferences

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.persistence.PreferencesManager
import ru.modemastudio.notepro.repository.FeatureRepository
import ru.modemastudio.notepro.util.appComponent
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var featureRepository: FeatureRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<MultiSelectListPreference>(preferencesManager.enabledFeaturesKey)?.apply {
            // TODO("Create listener for catching a select action and enable/disable feature in database")
        }
    }
}