package ru.modemastudio.notepro.persistence.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM ${Category.tableName}")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)
}