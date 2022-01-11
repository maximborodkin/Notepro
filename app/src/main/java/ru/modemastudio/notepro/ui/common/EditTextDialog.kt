package ru.modemastudio.notepro.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.AlertdialogEdittextBinding


class EditTextDialog(
    context: Context,
    title: String,
    text: String? = null,
    onPositiveButtonClicked: (title: String) -> Unit
) : MaterialAlertDialogBuilder(context) {

    init {
        setTitle(title)

        val customView = AlertdialogEdittextBinding.inflate(LayoutInflater.from(context))
        setView(customView.root)

        with(customView.alertDialogEditText) {
            setText(text)
            setSelection(length())
            requestFocus()
            addTextChangedListener { customView.alertDialogEditTextLayout.error = null }
        }

        setPositiveButton(R.string.ok, null)
        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = create()
        dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newText = customView.alertDialogEditText.text.toString()
            if (newText.isNotBlank()) {
                if (newText.length > 30) {
                    customView.alertDialogEditTextLayout.error =
                        context.getString(R.string.too_long_title)
                } else {
                    onPositiveButtonClicked(newText)
                    dialog.dismiss()
                }
            } else {
                customView.alertDialogEditTextLayout.error =
                    context.getString(R.string.title_is_required)
            }
        }
    }
}