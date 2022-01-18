package ru.modemastudio.notepro.persistence.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Category.Contract.Columns

@Dao
interface CategoryDao {

    @Query("SELECT * FROM ${Category.tableName}")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM ${Category.tableName} WHERE ${Columns.id}=:categoryId")
    fun getById(categoryId: Long): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    @Delete
    suspend fun delete(category: Category)
}