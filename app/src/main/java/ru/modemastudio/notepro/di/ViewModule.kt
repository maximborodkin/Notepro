package ru.modemastudio.notepro.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.modemastudio.notepro.repository.CategoryRepository
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.ui.notes_list.NotesListViewModel

@Module
object ViewModule {

    @Provides
    fun provideNotesListViewModel(
        application: Application,
        noteRepository: NoteRepository,
        categoryRepository: CategoryRepository
    ): NotesListViewModel {
        return NotesListViewModel.NotesListViewModelFactory(
            application,
            noteRepository,
            categoryRepository
        )
            .create(NotesListViewModel::class.java)
    }
}