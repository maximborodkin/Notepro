package ru.modemastudio.notepro.ui.notes_list

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.ItemNoteBinding
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.ui.notes_list.NotesListRecyclerAdapter.NoteViewHolder

class NotesListRecyclerAdapter(
    private val onItemClick: (noteId: Long) -> Unit,
    private val onItemSwipe: (noteId: Long) -> Unit
) : ListAdapter<Note, NoteViewHolder>(NoteDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        ItemTouchHelper(SwipeToDeleteCallback()).attachToRecyclerView(recyclerView)
    }

    class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.note = note
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).noteId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            itemView.setOnClickListener { onItemClick(getItem(bindingAdapterPosition).noteId) }
        }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) =
        holder.bind(getItem(position))

    object NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) =
            oldItem.noteId == newItem.noteId

        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }

    inner class SwipeToDeleteCallback : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
        override fun onMove(
            p0: RecyclerView,
            p1: RecyclerView.ViewHolder,
            p2: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) =
            onItemSwipe(getItem(viewHolder.bindingAdapterPosition).noteId)

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val context = viewHolder.itemView.context
            val swipeBackground = ColorDrawable(ContextCompat.getColor(context, R.color.swipe_red))
            val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete) ?: return

            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
            if (dX > 0) {
                swipeBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    dX.toInt(),
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.left + iconMargin,
                    itemView.top + iconMargin,
                    itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMargin
                )
            } else {
                swipeBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                    itemView.top + iconMargin,
                    itemView.right - iconMargin,
                    itemView.bottom - iconMargin
                )
            }
            c.save()

            swipeBackground.draw(c)
            if (dX > 0) {
                c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            } else {
                c.clipRect(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
            }
            deleteIcon.draw(c)
            c.restore()
        }
    }
}
