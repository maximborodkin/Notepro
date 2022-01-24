package ru.modemastudio.notepro

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.ui.notes_list.NotesListViewModel
import ru.modemastudio.notepro.util.adaptiveString
import ru.modemastudio.notepro.util.combineState
import ru.modemastudio.notepro.util.like
import timber.log.Timber
import java.util.*

@DelicateCoroutinesApi
@FlowPreview
class FilterTest {

    // Additional test data
    private val category1 = Category(1, "Category 1")
    private val category2 = Category(2, "Category 2")

    /**
     * Copy of [NotesListViewModel] internal filtering logic
     * */
    private val isDeletedShown = MutableStateFlow(false)
    private val searchQuery = MutableStateFlow<String?>(null)
    private val selectedCategories = MutableStateFlow(hashSetOf<Category>())
    private val notesRepository = object : NoteRepository {
        override suspend fun getAllNotes(): StateFlow<List<Note>> {
            val notes = listOf(
                Note(1, "Note number one", "Faster than a bullet", Date()),
                Note(2, "Note number two", "Terrifying scream", Date()),
                Note(3, "Note number three", "Enraged and full of anger", Date()),
                Note(4, "Note number four", "He's half man and half machine", Date()),
                Note(5, "Note number five", "Rides the Metal Monster", Date()),
                Note(6, "Note with category 1", "Breathing smoke and fire", Date(), category = category1),
                Note(7, "Note with category 2", "Closing in with vengeance soaring high", Date(), category = category2),
                Note(8, "Note with category 1", "He is the Painkiller", Date(), category = category1),
                Note(9, "Deleted note", "This is the Painkiller", Date(), isDeleted = true, category = category2),
            )
            return MutableStateFlow(notes)
        }

        override suspend fun create(title: String): Long = TODO()
        override suspend fun markAsDeleted(noteId: Long) = TODO()
        override suspend fun restore(noteId: Long) = TODO()
        override suspend fun getById(noteId: Long): Flow<Note?> = TODO()
        override suspend fun save(note: Note): Long = TODO()
        override suspend fun delete(note: Note) = TODO()
    }

    @FlowPreview
    suspend fun getAllNotes(): StateFlow<List<Note>> =
        notesRepository.getAllNotes()
            .combineState(isDeletedShown) { notes, isDeletedShown ->
                if (isDeletedShown) notes else notes.filterNot { it.isDeleted }
            }
            .combineState(searchQuery) { notes, searchQuery ->
                Timber.d(searchQuery)
                if (searchQuery.isNullOrBlank()) notes
                else notes.filter { note ->
                    note.title.like(searchQuery) ||
                            note.updatedAt.adaptiveString().like(searchQuery) ||
                            note.body.like(searchQuery) ||
                            note.category?.name.like(searchQuery)
                }
            }
            .combineState(selectedCategories) { notes, selectedCategories ->
                if (selectedCategories.isEmpty()) notes
                else notes.filter { it.category in selectedCategories }
            }

    @Test
    fun searchLineTest() = runBlocking {
        searchQuery.emit(null)
        isDeletedShown.emit(false)
        selectedCategories.emit(hashSetOf())

        assertEquals(8, getAllNotes().value.size)
        searchQuery.emit("number")
        assertEquals(5, getAllNotes().value.size)
    }

    @Test
    fun isDeletedShownTest() = runBlocking {
        searchQuery.emit(null)
        isDeletedShown.emit(false)
        selectedCategories.emit(hashSetOf())

        assertEquals(8, getAllNotes().value.size)
        isDeletedShown.emit(true)
        assertEquals(9, getAllNotes().value.size)
    }

    @Test
    fun selectedCategoriesTest() = runBlocking {
        searchQuery.emit(null)
        isDeletedShown.emit(false)
        selectedCategories.emit(hashSetOf())

        assertEquals(8, getAllNotes().value.size)
        selectedCategories.emit(hashSetOf(category1))
        assertEquals(2, getAllNotes().value.size)
    }

    @Test
    fun allFiltersTest() = runBlocking {
        searchQuery.emit(null)
        isDeletedShown.emit(false)
        selectedCategories.emit(hashSetOf())

        assertEquals(8, getAllNotes().value.size)

        searchQuery.emit("Painkiller")
        isDeletedShown.emit(true)
        selectedCategories.emit(hashSetOf(category2))

        assertEquals(1, getAllNotes().value.size)
        val category = getAllNotes().value.first()
        assertEquals(9, category.noteId)
    }
}