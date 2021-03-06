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
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentNotesListBinding
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.ui.common.CategoryChipsFactory
import ru.modemastudio.notepro.ui.common.EditTextDialog
import ru.modemastudio.notepro.util.appComponent
import ru.modemastudio.notepro.util.autoCleared
import javax.inject.Inject

@FlowPreview
class NotesListFragment : Fragment(R.layout.fragment_notes_list) {
    @Inject
    lateinit var model: NotesListViewModel
    private lateinit var binding: FragmentNotesListBinding
    private var recyclerAdapter by autoCleared<NotesListRecyclerAdapter>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
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
                model.viewModelScope.launch {
                    model.isDeletedShown.emit(!model.isDeletedShown.value)
                }
                activity?.invalidateOptionsMenu() // Redraw menu to show different delete icon
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
            if (model.isDeletedShown.value) R.drawable.ic_delete
            else R.drawable.ic_delete_off
        menu.findItem(R.id.notesListShowDeleted).setIcon(icon)

        with(menu.findItem(R.id.notesListSearchView).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    model.viewModelScope.launch {
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
        binding = FragmentNotesListBinding.bind(view)
        activity?.title = getString(R.string.app_name)
        recyclerAdapter = NotesListRecyclerAdapter(
            onItemClick = { noteId ->
                val action = NotesListFragmentDirections.actionNotesListToNoteDetails(noteId)
                findNavController().navigate(action)
            },
            onItemLongClick = { note ->
                EditTextDialog(
                    context = requireContext(),
                    title = getString(R.string.rename_note),
                    text = note.title,
                    onPositiveButtonClicked = { title ->
                        model.updateNote(note.copy(title = title))
                    }
                )
            },
            onItemDismiss = { noteId ->
                model.markNoteAsDeleted(noteId)
                Snackbar.make(
                    view,
                    getString(R.string.note_moved_to_trash),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(R.string.undo) {
                        model.restoreNote(noteId)
                    }
                    show()
                }
            },
            onItemDelete = { note, adapterPosition ->
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(getString(R.string.delete_note))
                    setMessage(getString(R.string.It_cant_be_undone))
                    setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        model.deleteNote(note)
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        // Redraw swiped item to restore item view
                        recyclerAdapter.notifyItemChanged(adapterPosition)
                        dialog.dismiss()
                    }
                    setOnCancelListener { recyclerAdapter.notifyItemChanged(adapterPosition) }
                    show()
                }
            },
            onItemRestore = { noteId ->
                model.restoreNote(noteId)
            }
        )

        with(binding) {
            notesListAddBtn.setOnClickListener {
                EditTextDialog(
                    context = requireContext(),
                    title = getString(R.string.create_note),
                    onPositiveButtonClicked = { title ->
                        model.viewModelScope.launch {
                            val noteId = model.createNote(title)
                            val action =
                                NotesListFragmentDirections.actionNotesListToNoteDetails(noteId)
                            findNavController().navigate(action)
                        }
                    })
            }
            lifecycleOwner = viewLifecycleOwner
            notesListRecycler.adapter = recyclerAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.getAllNotes().collect { notesList: List<Note> ->
                    binding.isEmpty = notesList.isEmpty()
                    recyclerAdapter.submitList(notesList)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.getAllCategories().collect { categoriesList: List<Category> ->
                    CategoryChipsFactory.createCategoryChips(
                        categories = categoriesList,
                        fragmentManager = childFragmentManager,
                        chipGroup = binding.notesListFilterChipsLayout,
                        isSingleSelection = false,
                        onCheckedChangeListener = { category, isChecked ->
                            val newCategories = hashSetOf<Category>().apply {
                                addAll(model.selectedCategories.value)
                                if (isChecked) add(category) else remove(category)
                            }
                            model.viewModelScope.launch {
                                model.selectedCategories.emit(newCategories)
                            }
                        },
                        isChecked = { category -> category in model.selectedCategories.value },
                        categoryActions = model
                    )
                }
            }
        }
    }
}