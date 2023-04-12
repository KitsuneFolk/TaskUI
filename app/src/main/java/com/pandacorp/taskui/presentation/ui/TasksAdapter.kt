package com.pandacorp.taskui.presentation.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.TaskItemBinding
import com.pandacorp.taskui.domain.models.TaskItem

class TasksAdapter(
    private val context: Context,
    val key: Int
) : ListAdapter<TaskItem, TasksAdapter.ViewHolder>(TaskDiffCallback()) {
    private val yellowColor by lazy {
        ContextCompat.getColor(context, R.color.yellow)
    }
    private val redColor by lazy {
        ContextCompat.getColor(context, R.color.red)
    }
    companion object {
        const val TAG = "TasksAdapter"
    }
    
    class TaskDiffCallback : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean =
            oldItem == newItem
    }
    
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
                binding = TaskItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false))
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskItem = getItem(position)
        holder.bind(taskItem)
    }
    
    //TODO: Add undo
    
    inner class ViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taskItem: TaskItem) {
            binding.mainTv.text = taskItem.text
            binding.timeTv.text = taskItem.time
            when (taskItem.priority) {
                TaskItem.WHITE -> binding.priorityImageView.setBackgroundColor(Color.WHITE)
                TaskItem.YELLOW -> binding.priorityImageView.setBackgroundColor(yellowColor)
                TaskItem.RED -> binding.priorityImageView.setBackgroundColor(redColor)
            }
            binding.completeButton.setOnClickListener {
                when (key) {
                    TaskItem.MAIN -> {
                        //TODO: Just implement different viewholder for completed and deleted tasks without completeImageButton
                        
                        // removeItem(position)
                        // dbHelper.removeById(DBHelper.MAIN_TASKS_TABLE_NAME, position)
                        // dbHelper.add(DBHelper.COMPLETED_TASKS_TABLE_NAME, taskItem)
                        // cancelNotification(taskItem)
                        // WidgetProvider.Companion.sendRefreshBroadcast(view!!.context)
                        // setUndoSnackbar(taskItem, position)
                    }
                    TaskItem.COMPLETED -> {}
                    TaskItem.DELETED -> {}
                }
            }
        }
    }
    
    override fun submitList(list: MutableList<TaskItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    
}