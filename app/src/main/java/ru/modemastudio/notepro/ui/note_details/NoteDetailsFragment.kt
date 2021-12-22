package ru.modemastudio.notepro.ui.note_details

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.data.DataUriSchemeHandler
import io.noties.markwon.image.svg.SvgMediaDecoder
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentNoteDetailsBinding
import ru.modemastudio.notepro.util.addViews
import ru.modemastudio.notepro.util.appComponent
import javax.inject.Inject

class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {
    private val binding by viewBinding(FragmentNoteDetailsBinding::bind)
    private val args: NoteDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var factory: NoteDetailsViewModel.NoteDetailsViewModelFactory.Factory
    private val model: NoteDetailsViewModel by viewModels { factory.create(args.noteId) }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        setHasOptionsMenu(true)
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_top_note_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.noteDetailsEdit) {
            // show/hide note editor
            model.isEditorVisible.postValue(model.isEditorVisible.value?.not())
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
            }
            val featureButtons = arrayListOf<Button>()
            model.features.observe(viewLifecycleOwner) {
                it.forEach { feature ->
                    val button = Button(requireContext()).apply {
                        text = feature.name.trim()
                        setOnClickListener {
                            // get current cursor position
                            val start = noteDetailsBottom.selectionStart.coerceAtLeast(0)
                            val end = noteDetailsBottom.selectionEnd.coerceAtLeast(0)

                            // place a tag
                            noteDetailsBottom.text?.replace(
                                start.coerceAtMost(end),
                                start.coerceAtLeast(end),
                                feature.tag
                            )

                            // set cursor to new position (depends of a feature)
                            val selectionStart = noteDetailsBottom.selectionStart -
                                    (feature.tag.length - feature.cursorOffset)
                            noteDetailsBottom.setSelection(
                                selectionStart,
                                selectionStart
                            )
                        }
                    }
                    featureButtons.add(button)
                }
                noteDetailsFeaturesLayout.addViews(featureButtons)
            }
        }
        initMarkwon()
    }

    override fun onPause() {
        super.onPause()
        model.save()
    }

    private fun initMarkwon() {
        val context = requireContext()
        super.onAttach(context)
        val markwon: Markwon = Markwon.builder(context)
            .usePlugin(ImagesPlugin.create { plugin ->
                plugin.addSchemeHandler(DataUriSchemeHandler.create())
                plugin.addMediaDecoder(SvgMediaDecoder.create(resources))
            })
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .build()
        val editor: MarkwonEditor = MarkwonEditor.create(markwon)
        binding.noteDetailsBottom.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
        binding.noteDetailsBottom.addTextChangedListener {
            binding.noteDetailsTop.text =
                markwon.toMarkdown(binding.noteDetailsBottom.text.toString())
        }
    }
}