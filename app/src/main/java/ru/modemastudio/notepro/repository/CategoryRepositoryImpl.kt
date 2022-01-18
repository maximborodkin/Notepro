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

    override suspend fun create(name: String): Category? = withContext(IO) {
        val categoryId = categoryDao.insert(Category(0, name))
        return@withContext categoryDao.getById(categoryId)
    }


    override suspend fun update(category: Category): Unit = withContext(IO) {
        categoryDao.insert(category)
    }

    override suspend fun delete(category: Category): Unit = withContext(IO) {
        categoryDao.delete(category)
    }

}