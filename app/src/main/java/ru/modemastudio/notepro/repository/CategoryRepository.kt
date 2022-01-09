package ru.modemastudio.notepro.repository

import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Category

interface CategoryRepository {

    suspend fun getAllCategories(): Flow<List<Category>>
}