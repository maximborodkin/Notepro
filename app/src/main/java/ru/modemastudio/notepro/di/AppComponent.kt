package ru.modemastudio.notepro.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.modemastudio.notepro.ui.MainActivity
import ru.modemastudio.notepro.ui.note_details.NoteDetailsFragment
import ru.modemastudio.notepro.ui.notes_list.NotesListFragment
import ru.modemastudio.notepro.ui.onboarding.OnboardingFragment
import ru.modemastudio.notepro.ui.preferences.PreferencesFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, ViewModule::class, UtilModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(notesListFragment: NotesListFragment)
    fun inject(preferencesFragment: PreferencesFragment)
    fun inject(noteDetailsFragment: NoteDetailsFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(onboardingFragment: OnboardingFragment)
}

