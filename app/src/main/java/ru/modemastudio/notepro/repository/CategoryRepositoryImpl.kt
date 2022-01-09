package ru.modemastudio.notepro.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.persistence.database.dao.CategoryDao
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun getAllCategories(): Flow<List<Category>> = withContext(IO) {
        categoryDao.getAllCategories()
    }
}