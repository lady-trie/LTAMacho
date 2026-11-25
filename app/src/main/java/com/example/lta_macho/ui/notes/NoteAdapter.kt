package com.example.lta_macho.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lta_macho.data.supabase.model.NoteModel
import com.example.lta_macho.databinding.ItemNoteBinding

class NoteAdapter(
    private val onEditClick: (NoteModel) -> Unit,
    private val onDeleteClick: (NoteModel) -> Unit
) : ListAdapter<NoteModel, NoteAdapter.NoteViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteModel) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteContent.text = note.content

            binding.btnEditNote.setOnClickListener {
                onEditClick(note)
            }

            binding.btnDeleteNote.setOnClickListener {
                onDeleteClick(note)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean =
            oldItem == newItem
    }
}