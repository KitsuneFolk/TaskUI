package com.pandacorp.taskui.data.database

import com.pandacorp.taskui.data.models.TaskDataItem
import com.pandacorp.taskui.domain.models.TaskItem
import javax.inject.Inject

class TasksMapper @Inject constructor() {
    fun toTaskItem(taskDataItem: TaskDataItem): TaskItem {
        return TaskItem(
                taskDataItem.id,
                taskDataItem.text,
                taskDataItem.time,
                taskDataItem.priority,
                taskDataItem.status)
    }
    
    fun toTaskDataItem(taskItem: TaskItem): TaskDataItem {
        return TaskDataItem(
                taskItem.id,
                taskItem.text,
                taskItem.time,
                taskItem.priority,
                taskItem.status)
    }
}