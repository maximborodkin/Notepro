package ru.modemastudio.notepro.ui.notes_list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentNotesListBinding
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.util.appComponent
import ru.modemastudio.notepro.util.autoCleared
import javax.inject.Inject

class NotesListFragment : Fragment(R.layout.fragment_notes_list) {
    @Inject
    lateinit var model: NotesListViewModel
    private val binding by viewBinding(FragmentNotesListBinding::bind)
    private var recyclerAdapter by autoCleared<NotesListRecyclerAdapter>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerAdapter = NotesListRecyclerAdapter(
            onItemClick = { noteId ->
                val action = NotesListFragmentDirections.actionNotesListToNoteDetails(noteId)
                findNavController().navigate(action)
            },
            onItemSwipe = { noteId ->
                model.markAsDeleted(noteId)
                Snackbar.make(
                    view,
                    getString(R.string.note_moved_to_trash),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(R.string.undo) {
                        model.restore(noteId)
                    }
                    show()
                }
            }
        )

        with(binding) {
            notesListAddBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    model.create()
                }
//                val action = NotesListFragmentDirections.actionNotesListToNoteDetails()
//                findNavController().navigate(action)
            }
            lifecycleOwner = viewLifecycleOwner
            notesListRecycler.adapter = recyclerAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.getAllNotes().collect { notesList: List<Note> ->
                    recyclerAdapter.submitList(notesList)
                    binding.isEmpty = notesList.isEmpty()
                }
            }
        }
    }
}