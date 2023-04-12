package com.pandacorp.taskui.domain.repositories

import com.pandacorp.taskui.domain.models.TaskItem

interface TasksRepository {
    fun getAll(): MutableList<TaskItem>
    fun insert(taskItem: TaskItem): Long
    fun remove(taskItem: TaskItem)
    fun update(taskItem: TaskItem)
    fun updateItems(list: List<TaskItem>)
    fun removeAll()
}