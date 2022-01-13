package ru.modemastudio.notepro.ui.notes_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.CategoryRepository
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.ui.common.CategoryActions
import ru.modemastudio.notepro.util.adaptiveString
import ru.modemastudio.notepro.util.dateTimeString
import ru.modemastudio.notepro.util.like
import timber.log.Timber

class NotesListViewModel(
    application: Application,
    private val notesRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : AndroidViewModel(application), CategoryActions {

    val isDeletedShown = MutableStateFlow(false)
    val searchQuery = MutableStateFlow<String?>(null)
    val selectedCategories = MutableStateFlow(hashSetOf<Category>())

    @FlowPreview
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
                    note.updatedAt.adaptiveString().like(searchQuery) ||
                    note.body.like(searchQuery) ||
                    note.category?.name.like(searchQuery)
                }
            }
            .combine(selectedCategories) { notes, selectedCategories ->
                if (selectedCategories.isEmpty()) notes
                else notes.filter { it.category in selectedCategories }
            }

    suspend fun createNote(title: String) = notesRepository.create(title)

    fun updateNote(note: Note) = viewModelScope.launch {
        notesRepository.save(note)
    }

    fun markNoteAsDeleted(noteId: Long) = viewModelScope.launch {
        notesRepository.markAsDeleted(noteId)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.delete(note)
    }

    fun restoreNote(noteId: Long) = viewModelScope.launch {
        notesRepository.restore(noteId)
    }

    suspend fun getAllCategories(): Flow<List<Category>> =
        categoryRepository.getAllCategories()

    override fun createCategory(name: String) {

        viewModelScope.launch {
            categoryRepository.create(name)
        }
    }

    override fun updateCategory(category: Category) {

        viewModelScope.launch {
            categoryRepository.update(category)
        }
    }

    override fun deleteCategory(category: Category) {

        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }

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