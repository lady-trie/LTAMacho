package com.example.lta_macho.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lta_macho.R
import com.example.lta_macho.data.supabase.model.TaskModel
import com.example.lta_macho.databinding.ItemTaskBinding

class TaskAdapter(
    private val onEditClick: (TaskModel) -> Unit,
    private val onDeleteClick: (TaskModel) -> Unit
) : ListAdapter<TaskModel, TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskModel) {
            val context = binding.root.context

            binding.tvTaskName.text = task.taskName
            binding.tvTaskDeadline.text = "Deadline: ${task.deadline}"
            binding.tvTaskStatus.text = task.status

            when (task.status.lowercase()) {
                "selesai" -> {
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_success))
                    binding.tvTaskStatus.setBackgroundResource(R.drawable.bg_badge_success)
                }
                "proses" -> {
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_warning))
                    binding.tvTaskStatus.setBackgroundResource(R.drawable.bg_badge_warning)
                }
                else -> {
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
                    binding.tvTaskStatus.setBackgroundResource(R.drawable.bg_badge_info)
                }
            }

            binding.btnEditTask.setOnClickListener {
                onEditClick(task)
            }

            binding.btnDeleteTask.setOnClickListener {
                onDeleteClick(task)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean =
            oldItem == newItem
    }
}