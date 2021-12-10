package ru.modemastudio.notepro.persistence.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.model.Note.Contract.Columns

@Dao
abstract class NoteDao {

    @Query("SELECT * FROM ${Note.tableName}")
    abstract fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM ${Note.tableName} WHERE ${Columns.noteId}=:noteId")
    abstract fun getById(noteId: Long): Flow<Note?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(note: Note): Long

    @Query("UPDATE ${Note.tableName} SET ${Columns.isDeleted}=1 WHERE ${Columns.noteId}=:noteId")
    abstract fun markAsDeleted(noteId: Long)

    @Query("UPDATE ${Note.tableName} SET ${Columns.isDeleted}=0 WHERE ${Columns.noteId}=:noteId")
    abstract fun restore(noteId: Long)

    @Delete
    abstract suspend fun delete(note: Note)
}