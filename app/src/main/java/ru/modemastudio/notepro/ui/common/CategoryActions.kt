package ru.modemastudio.notepro.ui.common

import ru.modemastudio.notepro.model.Category

interface CategoryActions {

    fun createCategory(name: String)
    fun updateCategory(category: Category)
    fun deleteCategory(category: Category)
}