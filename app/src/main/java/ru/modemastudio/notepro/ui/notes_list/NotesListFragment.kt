package ru.modemastudio.notepro.ui.notes_list

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State.STARTED
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
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_top_notes_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notesListSearchView -> true
            R.id.notesListShowDeleted -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    model.isDeletedShown.emit(!model.isDeletedShown.value)
                }
                activity?.invalidateOptionsMenu()
                true
            }
            R.id.notesListSettings -> {
                val action = NotesListFragmentDirections.actionNotesListToPreferences()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val icon =
            if (model.isDeletedShown.value) R.drawable.ic_delete else R.drawable.ic_delete_off
        menu.findItem(R.id.notesListShowDeleted).setIcon(icon)

        with(menu.findItem(R.id.notesListSearchView).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
                        model.searchQuery.emit(newText)
                    }
                    return true
                }
            })
            maxWidth = Int.MAX_VALUE
        }
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
            },
            onItemRestore = { noteId ->
                model.restore(noteId)
            }
        )

        with(binding) {
            notesListAddBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val noteId = model.create()
                    val action = NotesListFragmentDirections.actionNotesListToNoteDetails(noteId)
                    findNavController().navigate(action)
                }
            }
            lifecycleOwner = viewLifecycleOwner
            notesListRecycler.adapter = recyclerAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.getAllNotes().collect { notesList: List<Note> ->
                    //binding.isEmpty = notesList.isEmpty()
                    recyclerAdapter.submitList(notesList)
                }
            }
        }
    }
}