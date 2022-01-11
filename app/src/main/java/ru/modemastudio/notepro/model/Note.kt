package ru.modemastudio.notepro.model

import androidx.room.*
import ru.modemastudio.notepro.model.Note.Contract.tableName
import java.util.*

@Entity(tableName = tableName)
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.noteId)
    val noteId: Long,

    @ColumnInfo(name = Columns.title)
    var title: String,

    @ColumnInfo(name = Columns.body)
    var body: String,

    @ColumnInfo(name = Columns.updatedAt)
    var updatedAt: Date,

    @ColumnInfo(name = Columns.isDeleted)
    var isDeleted: Boolean = false,

    @Embedded(prefix = "reminder_")
    var reminder: Reminder? = null,

    @Embedded(prefix = "category_")
    var category: Category? = null
) {
    companion object Contract {
        const val tableName = "notes"

        object Columns {
            const val noteId = "note_id"
            const val title = "title"
            const val body = "body"
            const val updatedAt = "updated_at"
            const val isDeleted = "is_deleted"
        }
    }
}