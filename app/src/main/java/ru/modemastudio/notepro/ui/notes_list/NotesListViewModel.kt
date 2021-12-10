package ru.modemastudio.notepro.ui.notes_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.NoteRepository
import javax.inject.Inject

class NotesListViewModel @Inject constructor(
    application: Application,
    private val notesRepository: NoteRepository
) : AndroidViewModel(application) {

    val isDeletedShown = MutableStateFlow(false)

    suspend fun getAllNotes(): Flow<List<Note>> =
        notesRepository.getAllNotes().combine(isDeletedShown) { notes, isDeletedShown ->
            if (isDeletedShown) notes else notes.filterNot { it.isDeleted }
        }

    suspend fun create() = notesRepository.create("Title", "Body", null, null)

    fun markAsDeleted(noteId: Long) = viewModelScope.launch {
        notesRepository.markAsDeleted(noteId)
    }

    fun restore(noteId: Long) = viewModelScope.launch {
        notesRepository.restore(noteId)
    }

    class NotesListViewModelFactory(
        private val application: Application,
        private val notesRepository: NoteRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotesListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotesListViewModel(application, notesRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}