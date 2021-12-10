package ru.modemastudio.notepro.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.modemastudio.notepro.model.Category.Contract.tableName

@Entity(tableName = tableName)
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.id)
    val categoryId: Long,

    @ColumnInfo(name = Columns.name)
    val name: String
) {
    companion object Contract {
        const val tableName = "categories"

        object Columns {
            const val id = "category_id"
            const val name = "name"
        }
    }
}