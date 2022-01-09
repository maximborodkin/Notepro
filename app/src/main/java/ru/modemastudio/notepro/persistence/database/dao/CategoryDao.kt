package ru.modemastudio.notepro.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM ${Category.tableName}")
    fun getAllCategories(): Flow<List<Category>>
}