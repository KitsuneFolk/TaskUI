package com.pandacorp.taskui.domain.repositories

import com.pandacorp.taskui.domain.models.TaskItem

interface TasksRepository {
    fun getAll(): MutableList<TaskItem>
    fun insert(taskItem: TaskItem): Long
    fun insertList(taskItem: List<TaskItem>)
    fun remove(taskItem: TaskItem)
    fun removeList(list: List<TaskItem>)
    fun removeAll()
    fun update(taskItem: TaskItem)
    fun updateItems(list: List<TaskItem>)
}