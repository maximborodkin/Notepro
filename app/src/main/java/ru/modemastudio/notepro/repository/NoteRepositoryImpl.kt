package ru.modemastudio.notepro.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.model.Reminder
import ru.modemastudio.notepro.persistence.PreferencesManager
import ru.modemastudio.notepro.persistence.database.dao.NoteDao
import ru.modemastudio.notepro.util.dateTimeString
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val preferencesManager: PreferencesManager
) : NoteRepository {

    override suspend fun getAllNotes(): Flow<List<Note>> = withContext(IO) {
        noteDao.getAllNotes()
            .onCompletion { deleteExpiredNotes() }
    }

    private suspend fun deleteExpiredNotes() = withContext(IO) {
        if (preferencesManager.isAutodeleteEnabled){
            val autodeleteTime = preferencesManager.autodeleteTimeout
            noteDao.getAllNotes().collect { notes ->
                notes.forEach { note ->
                    if (Date().time - note.updatedAt.time > autodeleteTime) {
                        Timber.tag("EXPIRED_DELETION").i("""Note ${note.noteId}(${note.title}) was
                                deleted cause expiration. lastUpdate:${note.updatedAt.time},
                                expirationTime:${autodeleteTime}, now:${Date().time}""")
                        noteDao.delete(note)
                    }
                }
            }
        }
    }

    override suspend fun create(
        title: String
    ): Long = withContext(IO) {
        val note = Note(
            noteId = 0,
            title = title,
            body = String(),
            updatedAt = Date(),
            isDeleted = false,
            reminder = null,
            category = null
        )
        save(note)
    }

    override suspend fun save(note: Note): Long = withContext(IO) {
        noteDao.insert(note)
    }

    override suspend fun delete(note: Note) = withContext(IO) {
        noteDao.delete(note)
    }

    override suspend fun markAsDeleted(noteId: Long) = withContext(IO) {
        noteDao.markAsDeleted(noteId)
    }

    override suspend fun restore(noteId: Long) = withContext(IO) {
        noteDao.restore(noteId)
    }

    override suspend fun getById(noteId: Long) = withContext(IO) {
        noteDao.getById(noteId)
    }
}