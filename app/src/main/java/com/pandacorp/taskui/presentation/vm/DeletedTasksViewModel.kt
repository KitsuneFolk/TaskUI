package com.pandacorp.taskui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.AddTaskUseCase
import com.pandacorp.taskui.domain.usecases.AddTasksUseCase
import com.pandacorp.taskui.domain.usecases.GetTasksUseCase
import com.pandacorp.taskui.domain.usecases.RemoveTaskUseCase
import com.pandacorp.taskui.domain.usecases.RemoveTasksUseCase
import com.pandacorp.taskui.domain.usecases.UpdateTaskUseCase
import com.pandacorp.taskui.presentation.ui.fragments.DeletedTasksFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeletedTasksViewModel @Inject constructor(
    private val getItemsUseCase: GetTasksUseCase,
    private val updateItemUseCase: UpdateTaskUseCase,
    private val removeItemUseCase: RemoveTaskUseCase,
    private val removeItemsUseCase: RemoveTasksUseCase,
    private val addItemUseCase: AddTaskUseCase,
    private val addItemsUseCase: AddTasksUseCase
) : ViewModel() {

    private val _tasksList = MutableLiveData<MutableList<TaskItem>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getItemsUseCase().filter { it.status == TaskItem.DELETED }.toMutableList().apply {
                    postValue(this)
                }
            }
        }
    }
    val tasksList: LiveData<MutableList<TaskItem>> = _tasksList

    fun removeItem(taskItem: TaskItem) {
        _tasksList.value?.apply {
            remove(find { it.id == taskItem.id })
            _tasksList.postValue(this)
        }
        CoroutineScope(Dispatchers.IO).launch {
            removeItemUseCase(taskItem)
        }

    }

    fun removeAllForever() {
        val currentTasksList = _tasksList.value?.toMutableList()

        CoroutineScope(Dispatchers.IO).launch {
            currentTasksList?.let {
                removeItemsUseCase(it)
            }
        }

        _tasksList.value?.clear()
        _tasksList.postValue(_tasksList.value)
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

    fun moveItemToMain(taskItem: TaskItem) {
        taskItem.status = TaskItem.MAIN
        _tasksList.value?.apply {
            remove(find { it.id == taskItem.id })
            _tasksList.postValue(this)
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
    }

    /**
     * Add item to the livedata and update in Room
     */
    fun undo(position: Int, taskItem: TaskItem) {
        _tasksList.value?.add(position, taskItem)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
    }

    fun restoreItem(position: Int, taskItem: TaskItem) {
        _tasksList.value?.add(position, taskItem)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            addItemUseCase(taskItem)
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