package ru.modemastudio.notepro.ui.common

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.model.Category

object CategoryChipsFactory {

    fun createCategoryChips(
        categories: List<Category>,
        fragmentManager: FragmentManager,
        chipGroup: ChipGroup,
        isSingleSelection: Boolean,
        onCheckedChangeListener: (category: Category, isChecked: Boolean) -> Unit,
        isChecked: (category: Category) -> Boolean,
        categoryActions: CategoryActions
    ) {
        val context = chipGroup.context
        chipGroup.isSingleSelection = isSingleSelection

        chipGroup.removeAllViews()
        categories.forEach { category ->
            val categoryChip = createCategoryChip(
                context = chipGroup.context,
                category = category,
                onCheckedChangeListener = onCheckedChangeListener,
                isChecked = isChecked,
                categoryActions = categoryActions,
                fragmentManager = fragmentManager
            )
            chipGroup.addView(categoryChip)
        }

        val createCategoryChip = Chip(context).apply {
            text = context.getString(R.string.plus_symbol)
            isCheckable = false
            this.isChecked = false
            isCheckedIconVisible = false
            setOnClickListener {
                EditTextDialog(
                    context = context,
                    title = context.getString(R.string.create_category),
                    onPositiveButtonClicked = { name ->
                        categoryActions.createCategory(name)
                    }
                )
            }
        }
        chipGroup.addView(createCategoryChip)
    }

    private fun createCategoryChip(
        context: Context,
        category: Category,
        onCheckedChangeListener: (category: Category, isChecked: Boolean) -> Unit,
        isChecked: (category: Category) -> Boolean,
        categoryActions: CategoryActions,
        fragmentManager: FragmentManager
    ) = Chip(context).apply {
        text = category.name
        isCheckable = true
        this.isChecked = isChecked.invoke(category)
        isCheckedIconVisible = false
        chipBackgroundColor =
            ContextCompat.getColorStateList(context, R.color.chip_color_selector)

        setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener(category, isChecked) }

        setOnLongClickListener {
            CategoryMenuFragment.newInstance(
                category = category,
                onEdit = { editedCategory ->
                    categoryActions.updateCategory(editedCategory)
                },
                onDelete = { deletedCategory ->
                    categoryActions.deleteCategory(deletedCategory)
                }
            ).show(fragmentManager, CategoryMenuFragment::class.simpleName)
            true
        }
    }
}