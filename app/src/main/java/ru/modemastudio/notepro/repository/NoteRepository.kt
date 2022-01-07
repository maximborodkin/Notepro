package ru.modemastudio.notepro.repository

import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.model.Reminder

interface NoteRepository {

    suspend fun getAllNotes(): Flow<List<Note>>
    suspend fun create(title: String, body: String, reminder: Reminder?, category: Category?): Long
    suspend fun markAsDeleted(noteId: Long)
    suspend fun restore(noteId: Long)
    suspend fun getById(noteId: Long): Note?
    suspend fun save(note: Note): Long
    suspend fun delete(note: Note)
}