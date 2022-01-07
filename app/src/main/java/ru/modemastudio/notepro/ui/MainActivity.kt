package ru.modemastudio.notepro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.ActivityMainBinding
import ru.modemastudio.notepro.persistence.PreferencesManager
import ru.modemastudio.notepro.util.appComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.mainToolbar)
        appComponent.inject(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment

        val graph = navHostFragment.navController.navInflater.inflate(R.navigation.main_nav_graph)
        graph.startDestination = when {
            !preferencesManager.isOnboardingDone -> R.id.onboardingFragment
            else -> R.id.notesListFragment
        }
        navHostFragment.navController.graph = graph
    }
}