package com.pandacorp.taskui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.*
import com.pandacorp.taskui.presentation.ui.fragments.MainTasksFragment
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
    private val removeAllUseCase: RemoveAllTasksUseCase,
    private val updateItemUseCase: UpdateTaskUseCase,
    private val updateItemsUseCase: UpdateTasksUseCase
) :
    ViewModel() {
    companion object {
        private const val TAG = MainTasksFragment.TAG
    }
    
    private val _tasksList = MutableLiveData<MutableList<TaskItem>>().apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getItemsUseCase().filter { it.status == TaskItem.MAIN }.toMutableList().apply {
                    //TODO: Get only main tasks
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
            _tasksList.value?.add(0, taskItem)
            _tasksList.postValue(_tasksList.value)
        }
    }
    
    fun updateItem(position: Int, taskItem: TaskItem) {
        _tasksList.value?.set(position, taskItem)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
        
    }
    
    fun removeItem(position: Int, taskItem: TaskItem) {
        taskItem.status = TaskItem.DELETED
        _tasksList.value?.removeAt(position)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
        
    }
    
    fun removeAll() {
        _tasksList.value?.forEach { taskItem ->
            taskItem.status = TaskItem.DELETED
            
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateItemsUseCase(_tasksList.value!!)
        }
        _tasksList.value?.clear()
        _tasksList.postValue(_tasksList.value)
    }
    
    fun removeAllForever() {
        _tasksList.value?.clear()
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            removeAllUseCase()
        }
        
    }
}