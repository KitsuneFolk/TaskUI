package com.pandacorp.taskui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.AddTaskUseCase
import com.pandacorp.taskui.domain.usecases.AddTasksUseCase
import com.pandacorp.taskui.domain.usecases.GetTasksUseCase
import com.pandacorp.taskui.domain.usecases.RemoveTasksUseCase
import com.pandacorp.taskui.domain.usecases.UpdateTaskUseCase
import com.pandacorp.taskui.domain.usecases.UpdateTasksUseCase
import com.pandacorp.taskui.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainTasksViewModel @Inject constructor(
    private val getItemsUseCase: GetTasksUseCase,
    private val addItemUseCase: AddTaskUseCase,
    private val addItemsUseCase: AddTasksUseCase,
    private val removeItemsUseCase: RemoveTasksUseCase,
    private val updateItemUseCase: UpdateTaskUseCase,
    private val updateItemsUseCase: UpdateTasksUseCase
) :
    ViewModel() {
    private val _tasksList = MutableLiveData<MutableList<TaskItem>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getItemsUseCase().filter { it.status == TaskItem.MAIN }.toMutableList().apply {
                    postValue(this)
                }
            }
        }
    }
    val tasksList: LiveData<MutableList<TaskItem>> = _tasksList

    fun addItem(taskItem: TaskItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = addItemUseCase(taskItem)
            taskItem.id = id
            _tasksList.value?.add(Constants.ITEM_ADD_POSITION, taskItem)
            _tasksList.postValue(_tasksList.value)
        }
    }

    /**
     * Method that add the task only in the liveData
     */
    fun addItemVmOnly(taskItem: TaskItem) {
        _tasksList.value?.add(Constants.ITEM_ADD_POSITION, taskItem)
        _tasksList.postValue(_tasksList.value)
    }

    fun removeItem(taskItem: TaskItem) {
        taskItem.status = TaskItem.DELETED
        _tasksList.value?.apply {
            remove(find { it.id == taskItem.id })
            _tasksList.postValue(this)
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
    }

    fun removeAll() {
        val currentTasksList =
            _tasksList.value?.map { taskItem -> taskItem.copy(status = TaskItem.DELETED) }?.toMutableList()
        CoroutineScope(Dispatchers.IO).launch {
            currentTasksList?.let {
                updateItemsUseCase(it)
            }
        }
        _tasksList.apply {
            value?.clear()
            postValue(_tasksList.value)
        }
    }

    fun removeAllForever() {
        val currentTasksList = _tasksList.value?.toMutableList()!!
        CoroutineScope(Dispatchers.IO).launch {
            tasksList.value?.let {
                removeItemsUseCase(currentTasksList)
            }
        }
        _tasksList.apply {
            value?.clear()
            postValue(_tasksList.value)
        }
    }


    fun completeItem(taskItem: TaskItem) {
        taskItem.status = TaskItem.COMPLETED
        _tasksList.value?.apply {
            remove(find { it.id == taskItem.id })

            _tasksList.postValue(this)
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
    }

    /**
     * Method that completes the task only in the liveData(In case AppWidgetProvider already updated in Room)
     */
    fun completeItemVmOnly(taskItem: TaskItem) {
        taskItem.status = TaskItem.COMPLETED
        _tasksList.value?.apply {
            remove(find { it.id == taskItem.id })
            _tasksList.postValue(this)
        }
    }

    /**
     * Add item to the livedata and update in Room
     */
    fun restoreItem(position: Int, taskItem: TaskItem) {
        _tasksList.value?.add(position, taskItem)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
    }

    /**
     * Put all the given items to the livedata and update the items in Room
     */
    fun undoRemoveAll(tasksList: MutableList<TaskItem>) {
        _tasksList.value?.addAll(tasksList)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateItemsUseCase(tasksList)
        }
    }

    fun undoRemoveAllForever(tasksList: MutableList<TaskItem>) {
        _tasksList.value?.addAll(tasksList)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            addItemsUseCase(tasksList)
        }
    }
}