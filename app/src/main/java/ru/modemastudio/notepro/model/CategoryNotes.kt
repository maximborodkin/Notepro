package ru.modemastudio.notepro.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryNotes(
    @Embedded val category: Category,

    @Relation(
        parentColumn = Category.Contract.Columns.id,
        entityColumn= Note.Contract.Columns.noteId,
        entity = Note::class
    )
    val notes: List<Note>
)