package com.debugger.jetpack.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.debugger.jetpack.data.Task
import com.debugger.jetpack.databinding.ItemTasksBinding

class TaskAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {


    inner class TaskViewHolder(private val binding: ItemTasksBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val task = getItem(adapterPosition)

                        listener.onItemClick(task)
                    }
                }
                cbTask.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val task = getItem(adapterPosition)

                        listener.onCheckBoxClick(task, cbTask.isChecked)
                    }
                }

            }
        }

        fun bind(task: Task) {
            binding.apply {
                cbTask.isChecked = task.completed
                tvTasks.text = task.name
                tvTasks.paint.isStrikeThruText = task.completed
                ivImportant.isVisible = task.important
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTasksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }

}