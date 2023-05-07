package com.pandacorp.taskui.data.repositories

import com.pandacorp.taskui.data.database.TasksDao
import com.pandacorp.taskui.data.mappers.TasksMapper
import com.pandacorp.taskui.data.models.TaskDataItem
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.repositories.TasksRepository
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(private val dao: TasksDao, private val mapper: TasksMapper) :
    TasksRepository {
    override fun getAll(): MutableList<TaskItem> =
        dao.getAll().map { mapper.toTaskItem(it) }.toMutableList()

    override fun insert(taskItem: TaskItem): Long {
        return dao.insert(mapper.toTaskDataItem(taskItem))
    }

    override fun insertList(taskItem: List<TaskItem>) {
        val newList = mutableListOf<TaskDataItem>()
        taskItem.forEach {
            newList.add(mapper.toTaskDataItem(it))
        }
        return dao.insertList(newList)
    }

    override fun remove(taskItem: TaskItem) {
        dao.remove(mapper.toTaskDataItem(taskItem))
    }

    override fun removeList(list: List<TaskItem>) {
        val mappedList = list.map {
            mapper.toTaskDataItem(it)
        }
        dao.removeList(mappedList)
    }

    override fun update(taskItem: TaskItem) {
        dao.update(mapper.toTaskDataItem(taskItem))
    }

    override fun updateItems(list: List<TaskItem>) {
        val newList = mutableListOf<TaskDataItem>()
        list.forEach {
            newList.add(mapper.toTaskDataItem(it))
        }
        dao.updateList(newList)
    }

    override fun removeAll() {
        dao.removeAll()
    }
}