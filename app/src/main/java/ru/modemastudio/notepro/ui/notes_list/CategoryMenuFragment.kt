package ru.modemastudio.notepro.ui.notes_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentBottomCategoryMenuBinding
import ru.modemastudio.notepro.model.Category

class CategoryMenuFragment(
    private val category: Category,
    private val onEdit: (category: Category) -> Unit,
    private val onDelete: (category: Category) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomCategoryMenuBinding.inflate(layoutInflater, container, false)

        binding.categoryMenuEditBtn.setOnClickListener {
            EditTextDialog(
                context = requireContext(),
                title = getString(R.string.rename_category),
                text = category.name,
                onPositiveButtonClicked = { newName ->
                    onEdit(category.copy(name = newName))
                    dismiss()
                }
            )
        }

        binding.categoryMenuDelete.setOnClickListener {
            onDelete(category)
            dismiss()
        }

        return binding.root
    }
}