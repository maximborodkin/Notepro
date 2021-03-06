package ru.modemastudio.notepro.ui.note_details

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.data.DataUriSchemeHandler
import io.noties.markwon.image.svg.SvgMediaDecoder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentNoteDetailsBinding
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.ui.common.CategoryChipsFactory
import ru.modemastudio.notepro.util.appComponent
import javax.inject.Inject

class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {
    private val binding by viewBinding(FragmentNoteDetailsBinding::bind)
    private val args: NoteDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var factory: NoteDetailsViewModel.NoteDetailsViewModelFactory.Factory
    private val model: NoteDetailsViewModel by viewModels { factory.create(args.noteId) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_top_note_details, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val icon =
            if (model.isEditorVisible.value == true) R.drawable.ic_edit
            else R.drawable.ic_edit_off
        menu.findItem(R.id.noteDetailsEdit).setIcon(icon)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.noteDetailsEdit) {
            // Show/hide note editor
            model.isEditorVisible.postValue(model.isEditorVisible.value?.not())
            activity?.invalidateOptionsMenu() // Redraw menu to show different edit icon
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
            model.note.observe(viewLifecycleOwner) {
                activity?.title = it.title
                initCategoriesChips()
            }
            model.isEditorVisible.observe(viewLifecycleOwner) {
                // Redraw menu to show different edit icon if isEditorVisible was changed from ViewModel
                activity?.invalidateOptionsMenu()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    model.getEnabledFeatures().collect { features ->
                        noteDetailsFeaturesChipsGroup.removeAllViews()
                        features.forEach { feature ->
                            noteDetailsFeaturesChipsGroup.addView(createFeatureChip(feature))
                        }
                    }
                }
            }
        }
        initMarkwon()
    }

    override fun onPause() {
        super.onPause()
        model.saveNote()
    }

    private fun createFeatureChip(feature: Feature) = Chip(requireContext()).apply {
        text = feature.name.trim()
        isCheckable = false
        setOnClickListener {
            // Get current cursor position
            val start = binding.noteDetailsEditText.selectionStart.coerceAtLeast(0)
            val end = binding.noteDetailsEditText.selectionEnd.coerceAtLeast(0)

            // Place a tag
            binding.noteDetailsEditText.text?.replace(
                start.coerceAtMost(end),
                start.coerceAtLeast(end),
                feature.tag
            )

            // Set cursor to new position (depends of a feature)
            val selectionStart = binding.noteDetailsEditText.selectionStart -
                    (feature.tag.length - feature.cursorOffset)
            binding.noteDetailsEditText.setSelection(
                selectionStart,
                selectionStart
            )
        }
    }

    private fun initCategoriesChips() {
        binding.noteDetailsCategoryChipsGroup.isSingleSelection = true
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.getAllCategories().collect { categories ->
                    CategoryChipsFactory.createCategoryChips(
                        categories = categories,
                        fragmentManager = childFragmentManager,
                        chipGroup = binding.noteDetailsCategoryChipsGroup,
                        isSingleSelection = true,
                        onCheckedChangeListener = { category, isChecked ->
                            if (isChecked) {
                                model.note.value?.category = category
                            } else {
                                if (model.note.value?.category == category) {
                                    model.note.value?.category = null
                                }
                            }
                        },
                        isChecked = { category -> model.note.value?.category == category },
                        categoryActions = model
                    )
                }
            }
        }
    }

    private fun initMarkwon() {
        val context = requireContext()

        val tableTheme = TableTheme.buildWithDefaults(context)
            .tableBorderColor(Color.WHITE)
            .tableCellPadding(20)
            .tableHeaderRowBackgroundColor(Color.rgb(150, 150, 150)) // Light gray
            .build()

        val markwon: Markwon = Markwon.builder(context)
            .usePlugin(ImagesPlugin.create { plugin ->
                plugin.addSchemeHandler(DataUriSchemeHandler.create())
                plugin.addMediaDecoder(SvgMediaDecoder.create(resources))
            })
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TablePlugin.create(tableTheme))
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .build()
        val editor: MarkwonEditor = MarkwonEditor.create(markwon)
        binding.noteDetailsEditText.addTextChangedListener(
            MarkwonEditorTextWatcher.withProcess(
                editor
            )
        )
        binding.noteDetailsEditText.addTextChangedListener {
            binding.noteDetailsTextView.text =
                markwon.toMarkdown(binding.noteDetailsEditText.text.toString())
        }
    }
}