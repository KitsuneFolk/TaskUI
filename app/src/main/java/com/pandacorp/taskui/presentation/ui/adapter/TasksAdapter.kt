package com.pandacorp.taskui.presentation.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.CompletedTaskItemBinding
import com.pandacorp.taskui.databinding.MainTaskItemBinding
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.presentation.notifications.NotificationUtils
import com.pandacorp.taskui.presentation.widget.WidgetProvider
import java.text.DateFormat
import java.util.Locale

class TasksAdapter(private val context: Context, val key: Int) :
    ListAdapter<TaskItem, ViewHolder>(TaskDiffCallback()) {

    interface TaskAdapterListener {
        fun onCompleteButtonClicked(position: Int, taskItem: TaskItem)
    }

    private val whiteColor by lazy {
        Color.WHITE
    }
    private val yellowColor by lazy {
        ContextCompat.getColor(context, R.color.yellow)
    }
    private val redColor by lazy {
        ContextCompat.getColor(context, R.color.red)
    }
    private val dateFormatter =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
    var taskAdapterListener: TaskAdapterListener? = null

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean =
            oldItem == newItem
    }

    inner class MainTaskViewHolder(val binding: MainTaskItemBinding) :
        ViewHolder(binding.root) {
        fun bind(taskItem: TaskItem) {
            // Reset the properties for the proper recycling
            binding.apply {
                timeTv.visibility = View.GONE
                priorityCardView.visibility = View.VISIBLE
                taskTitle.text = taskItem.text
                taskItem.time?.let {
                    timeTv.visibility = View.VISIBLE
                    timeTv.text = dateFormatter.format(it)
                }
                when (taskItem.priority) {
                    TaskItem.WHITE -> priorityCardView.setCardBackgroundColor(whiteColor)
                    TaskItem.YELLOW -> priorityCardView.setCardBackgroundColor(yellowColor)
                    TaskItem.RED -> priorityCardView.setCardBackgroundColor(redColor)
                    null -> priorityCardView.visibility = View.GONE
                }
                completeButton.setOnClickListener {
                    when (key) {
                        TaskItem.MAIN -> {
                            NotificationUtils.cancel(context, taskItem)
                            taskAdapterListener?.onCompleteButtonClicked(bindingAdapterPosition, taskItem)
                            WidgetProvider.update(context)
                        }

                        TaskItem.DELETED -> {
                            taskAdapterListener?.onCompleteButtonClicked(
                                bindingAdapterPosition,
                                taskItem
                            )
                        }
                    }
                }
            }
        }
    }

    inner class CompletedTaskViewHolder(val binding: CompletedTaskItemBinding) : ViewHolder(binding.root) {
        fun bind(taskItem: TaskItem) {
            binding.apply {
                // Reset the properties for proper recycling
                timeTv.visibility = View.GONE
                priorityCardView.visibility = View.VISIBLE
                taskTitle.text = taskItem.text
                taskItem.time?.let {
                    timeTv.visibility = View.VISIBLE
                    timeTv.text = dateFormatter.format(it)
                }
                when (taskItem.priority) {
                    TaskItem.WHITE -> priorityCardView.setCardBackgroundColor(whiteColor)
                    TaskItem.YELLOW -> priorityCardView.setCardBackgroundColor(yellowColor)
                    TaskItem.RED -> priorityCardView.setCardBackgroundColor(redColor)
                    null -> priorityCardView.visibility = View.GONE
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (key) {
            TaskItem.MAIN -> {
                MainTaskViewHolder(
                    binding = MainTaskItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            TaskItem.COMPLETED -> {
                CompletedTaskViewHolder(
                    binding = CompletedTaskItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            TaskItem.DELETED -> {
                MainTaskViewHolder(
                    binding = MainTaskItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid key: $key")
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskItem = getItem(position)

        when (holder) {
            is MainTaskViewHolder -> {
                holder.bind(taskItem)
            }

            is CompletedTaskViewHolder -> {
                holder.bind(taskItem)
            }

            else -> throw IllegalArgumentException("Invalid viewHolder: $holder")
        }
    }

    override fun submitList(list: MutableList<TaskItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

}