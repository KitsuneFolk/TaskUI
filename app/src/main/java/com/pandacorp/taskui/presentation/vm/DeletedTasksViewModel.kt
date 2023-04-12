package com.pandacorp.taskui.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.taskui.domain.models.TaskItem
import com.pandacorp.taskui.domain.usecases.GetTasksUseCase
import com.pandacorp.taskui.domain.usecases.RemoveAllTasksUseCase
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
    private val removeAllUseCase: RemoveAllTasksUseCase,
    private val updateItemUseCase: UpdateTaskUseCase
) : ViewModel() {
    companion object {
        private const val TAG = DeletedTasksFragment.TAG
    }
    
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
    
    fun removeItem(position: Int, taskItem: TaskItem) {
        taskItem.status = TaskItem.DELETED
        _tasksList.value?.removeAt(position)
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            updateItemUseCase(taskItem)
        }
        
    }
    
    fun removeAllForever() {
        _tasksList.value?.clear()
        _tasksList.postValue(_tasksList.value)
        CoroutineScope(Dispatchers.IO).launch {
            removeAllUseCase()
        }
        
    }
}