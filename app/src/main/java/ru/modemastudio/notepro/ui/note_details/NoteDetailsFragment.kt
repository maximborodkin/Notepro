package ru.modemastudio.notepro.ui.note_details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentNoteDetailsBinding

class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {
    private val binding by viewBinding(FragmentNoteDetailsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}