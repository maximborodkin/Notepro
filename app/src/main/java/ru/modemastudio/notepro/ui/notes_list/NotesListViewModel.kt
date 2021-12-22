package ru.modemastudio.notepro.ui.notes_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.util.dateTimeString
import ru.modemastudio.notepro.util.like
import timber.log.Timber
import java.util.*

class NotesListViewModel(
    application: Application,
    private val notesRepository: NoteRepository
) : AndroidViewModel(application) {

    val isDeletedShown = MutableStateFlow(false)
    val searchQuery = MutableStateFlow<String?>(null)

    suspend fun getAllNotes(): Flow<List<Note>> =
        notesRepository.getAllNotes()
            .combine(isDeletedShown) { notes, isDeletedShown ->
                if (isDeletedShown) notes else notes.filterNot { it.isDeleted }
            }
            .combine(searchQuery.debounce(200).map { it?.lowercase() }) { notes, searchQuery ->
                Timber.d(searchQuery)
                if (searchQuery.isNullOrBlank()) notes
                else notes.filter { note ->
                    note.title.like(searchQuery) ||
                            note.body.like(searchQuery) ||
                            note.category?.name.like(searchQuery) ||
                            note.updatedAt.dateTimeString().like(searchQuery) ||
                            note.reminder?.date?.dateTimeString().like(searchQuery) ||
                            note.reminder?.priority?.name.like(searchQuery)
                }
            }

    suspend fun create() = notesRepository.create(Date().dateTimeString(), String(), null, null)

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