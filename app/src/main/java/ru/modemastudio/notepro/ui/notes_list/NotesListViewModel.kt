package ru.modemastudio.notepro.ui.notes_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.repository.CategoryRepository
import ru.modemastudio.notepro.util.dateTimeString
import ru.modemastudio.notepro.util.like
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class NotesListViewModel(
    application: Application,
    private val notesRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : AndroidViewModel(application) {

    val isDeletedShown = MutableStateFlow(false)
    val searchQuery = MutableStateFlow<String?>(null)

    val selectedCategories = MutableStateFlow(HashSet<Category>())

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
            .combine(selectedCategories) { notes, selectedCategories ->
                if (selectedCategories.isEmpty()) notes
                else notes.filter { it.category in selectedCategories }
            }

    suspend fun getAllCategories(): Flow<List<Category>> =
        categoryRepository.getAllCategories()

    fun updateCategories(newCategories: HashSet<Category>) = viewModelScope.launch(IO) {
        selectedCategories.emit(newCategories)
    }

    suspend fun create(title: String?) =
        notesRepository.create(title ?: Date().dateTimeString(), String(), null, null)

    fun markAsDeleted(noteId: Long) = viewModelScope.launch {
        notesRepository.markAsDeleted(noteId)
    }

    fun delete(note: Note) = viewModelScope.launch {
        notesRepository.delete(note)
    }

    fun restore(noteId: Long) = viewModelScope.launch {
        notesRepository.restore(noteId)
    }

    suspend fun getById(noteId: Long): Note? = notesRepository.getById(noteId)

    fun update(note: Note) = viewModelScope.launch { notesRepository.save(note) }

    class NotesListViewModelFactory(
        private val application: Application,
        private val notesRepository: NoteRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotesListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotesListViewModel(application, notesRepository, categoryRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}