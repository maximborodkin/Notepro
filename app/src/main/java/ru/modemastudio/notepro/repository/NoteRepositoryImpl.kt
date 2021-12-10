package ru.modemastudio.notepro.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.model.Reminder
import ru.modemastudio.notepro.persistence.database.dao.NoteDao
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(private val noteDao: NoteDao) : NoteRepository {

    override suspend fun getAllNotes(): Flow<List<Note>> = withContext(IO) { noteDao.getAllNotes() }

    override suspend fun create(
        title: String,
        body: String,
        reminder: Reminder?,
        category: Category?
    ) : Long = withContext(IO) {
        val note = Note(
            noteId = 0,
            title = title,
            body = body,
            updatedAt = Date(),
            isDeleted = false,
            reminder = reminder,
            category = category
        )
        noteDao.insert(note)
    }

    override suspend fun markAsDeleted(noteId: Long) = withContext(IO) {
        noteDao.markAsDeleted(noteId)
    }

    override suspend fun restore(noteId: Long) = withContext(IO) {
        noteDao.restore(noteId)
    }
}