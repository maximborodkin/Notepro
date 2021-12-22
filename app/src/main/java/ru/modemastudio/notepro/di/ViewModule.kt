package ru.modemastudio.notepro.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.modemastudio.notepro.repository.FeatureRepository
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.ui.note_details.NoteDetailsViewModel
import ru.modemastudio.notepro.ui.notes_list.NotesListViewModel

@Module
object ViewModule {

    @Provides
    fun provideNotesListViewModel(
        application: Application,
        noteRepository: NoteRepository
    ): NotesListViewModel {
        return NotesListViewModel.NotesListViewModelFactory(application, noteRepository)
            .create(NotesListViewModel::class.java)
    }
}