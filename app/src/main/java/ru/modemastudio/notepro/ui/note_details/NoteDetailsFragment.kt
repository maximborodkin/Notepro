package ru.modemastudio.notepro.ui.note_details

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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

class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {
    private val binding by viewBinding(FragmentNoteDetailsBinding::bind)
    private val currentNote = String()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMarkwon()
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
        binding.noteDetailsTop.text = markwon.toMarkdown(currentNote)
    }
}