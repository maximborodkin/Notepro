package ru.modemastudio.notepro.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentBottomCategoryMenuBinding
import ru.modemastudio.notepro.model.Category

class CategoryMenuFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomCategoryMenuBinding.inflate(layoutInflater, container, false)

        binding.categoryMenuEditBtn.setOnClickListener {
            EditTextDialog(
                context = binding.root.context,
                title = getString(R.string.rename_category),
                text = category.name,
                onPositiveButtonClicked = { newName ->
                    onEdit(category.copy(name = newName))
                    dismiss()
                }
            )
        }

        val deletionAlertDialog = MaterialAlertDialogBuilder(binding.root.context).also {
            it.setTitle(R.string.delete_category)
            it.setMessage(R.string.delete_category_message)
            it.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            it.setPositiveButton(R.string.ok) { dialog, _ ->
                onDelete(category)
                dialog.dismiss()
                dismiss()
            }
        }

        binding.categoryMenuDelete.setOnClickListener { deletionAlertDialog.show() }

        return binding.root
    }

    companion object {
        private lateinit var category: Category
        private lateinit var onEdit: (category: Category) -> Unit
        private lateinit var onDelete: (category: Category) -> Unit

        fun newInstance(
            category: Category,
            onEdit: (category: Category) -> Unit,
            onDelete: (category: Category) -> Unit
        ): CategoryMenuFragment {
            Companion.category = category
            Companion.onEdit = onEdit
            Companion.onDelete = onDelete
            return CategoryMenuFragment()
        }
    }
}