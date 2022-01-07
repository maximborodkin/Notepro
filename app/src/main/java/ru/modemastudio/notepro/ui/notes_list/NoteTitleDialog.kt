package ru.modemastudio.notepro.ui.notes_list

import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager.LayoutParams
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.updatePadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.modemastudio.notepro.R


class NoteTitleDialog(
    context: Context,
    title: String?,
    onPositiveButtonClicked: (title: String) -> Unit
) : MaterialAlertDialogBuilder(context) {

    init {
        val dialogTitle = if (title.isNullOrBlank()) R.string.create_note else R.string.rename_note
        setTitle(context.getString(dialogTitle))

        // The container used for setting margins for the editText
        val container = LinearLayoutCompat(context).apply {
            updatePadding(left = 50, right = 50)
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        val editText = EditText(context).apply {
            maxLines = 10
            layoutParams = LinearLayoutCompat.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            requestFocus()
            setText(title)
            setSelection(length())
        }
        container.addView(editText)
        setView(container)

        setPositiveButton(R.string.ok) { dialog, _ ->
            val newTitle = editText.text.toString()
            if (newTitle.isNotBlank()) {
                onPositiveButtonClicked(newTitle)
                dialog.dismiss()
            }
        }
        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = create()
        dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }
}